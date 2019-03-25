package io.github.anycollect;

import io.github.anycollect.core.api.Processor;
import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.Writer;
import io.github.anycollect.core.api.internal.PullManager;
import io.github.anycollect.core.api.target.SelfDiscovery;
import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.core.impl.pull.PullManagerImpl;
import io.github.anycollect.core.impl.router.StdRouter;
import io.github.anycollect.core.impl.router.config.ImmutableRouterConfig;
import io.github.anycollect.core.impl.router.config.RouterConfig;
import io.github.anycollect.core.impl.self.SelfDiscoveryConfig;
import io.github.anycollect.core.impl.self.StdSelfDiscovery;
import io.github.anycollect.extensions.EnvVarSubstitutor;
import io.github.anycollect.extensions.InstanceLoader;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.definitions.*;
import io.github.anycollect.extensions.snakeyaml.YamlInstanceLoader;
import io.github.anycollect.meter.registry.AnyCollectMeterRegistry;
import io.github.anycollect.meter.registry.AnyCollectMeterRegistryConfig;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.noop.NoopMeterRegistry;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Extension(name = CoreLoader.NAME, point = InstanceLoader.class)
public final class CoreLoader implements InstanceLoader {
    public static final String NAME = "Core";
    private static final Logger LOG = LoggerFactory.getLogger(CoreLoader.class);
    private final CoreConfig config;
    private final Scope scope;

    @ExtCreator
    public CoreLoader(@ExtDependency(qualifier = "parent") @Nonnull final InstanceLoader parent,
                      @ExtConfig @Nonnull final CoreConfig config) {
        this.config = config;
        this.scope = new SimpleScope(parent.getScope(), "core");
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void load(@Nonnull final ExtendableContext context) {
        SelfDiscovery selfDiscovery = new StdSelfDiscovery(SelfDiscoveryConfig.builder()
                .targetId("anycollect-self")
                .build());
        Definition meterRegistryDefinition;
        MeterRegistry meterRegistry;
        if (config.internalMonitoring().logic().enabled()) {
            meterRegistryDefinition = context.getDefinition(AnyCollectMeterRegistry.NAME);
            meterRegistry = new AnyCollectMeterRegistry(
                    AnyCollectMeterRegistryConfig.builder()
                    .commonTags(config.internalMonitoring().tags())
                    .commonMeta(config.internalMonitoring().meta())
                    .globalPrefix(config.internalMonitoring().prefix())
                    .build()
            );
        } else {
            meterRegistry = new NoopMeterRegistry();
            try {
                meterRegistryDefinition = Definition.builder()
                        .withExtension(MeterRegistry.class, NoopMeterRegistry.class.getConstructor())
                        .withName("NoopMeterRegistry")
                        .build();
                context.addDefinition(meterRegistryDefinition);
            } catch (NoSuchMethodException e) {
                throw new ConfigurationException("noop meter registry must have default constructor", e);
            }
        }
        Instance meterRegistryInstance = new Instance(
                meterRegistryDefinition,
                "meterRegistry",
                meterRegistry,
                InjectMode.AUTO,
                Priority.DEFAULT,
                scope
        );

        PullManager pullManager = new PullManagerImpl(
                selfDiscovery,
                meterRegistry,
                config.pull()
        );
        InternalReader internalReader = new InternalReader(pullManager, meterRegistry, config.internalMonitoring());
        Instance internalReaderInstance = new Instance(
                context.getDefinition(InternalReader.NAME),
                "internal",
                internalReader,
                InjectMode.AUTO,
                Priority.DEFAULT,
                scope
        );

        Instance pullManagerInstance = new Instance(
                context.getDefinition(PullManagerImpl.NAME),
                "pullManager",
                pullManager,
                InjectMode.AUTO,
                Priority.DEFAULT,
                scope
        );

        context.addInstance(meterRegistryInstance);
        context.addInstance(pullManagerInstance);
        context.addInstance(internalReaderInstance);
        for (Export export : config.export()) {
            File includeConfigFile;
            String configName = export.file();
            if (!new File(configName).isAbsolute()) {
                Scope current = scope;
                while (current != null && !(current instanceof FileScope)) {
                    current = current.getParent();
                }
                if (current != null) {
                    File parentLoaderFile = ((FileScope) current).getFile();
                    includeConfigFile = FileUtils.getFile(parentLoaderFile.getParentFile(), configName);
                } else {
                    throw new ConfigurationException("relative path is not supported, could not find " + configName);
                }
            } else {
                includeConfigFile = new File(configName);
            }
            YamlInstanceLoader loader;
            Scope childScope = FileScope.child(this.scope, includeConfigFile);
            try {
                loader = new YamlInstanceLoader(childScope, new FileReader(includeConfigFile), new EnvVarSubstitutor());
            } catch (FileNotFoundException e) {
                throw new ConfigurationException("configuration file " + includeConfigFile.getAbsolutePath()
                        + "is not found", e);
            }
            context.addInstance(new Instance(context.getDefinition(YamlInstanceLoader.NAME),
                    configName, loader, InjectMode.AUTO, Priority.DEFAULT, scope));
            LOG.info("Start child instance loader {}", loader);
            loader.load(context);
            for (String instance : export.instances()) {
                Instance baseInstance = context.getInstance(instance, childScope);
                if (baseInstance == null) {
                    throw new ConfigurationException("could not export instance " + instance);
                }
                LOG.info("Export instance {}.{} from {}.{}", scope, instance, baseInstance.getScope(), instance);
                context.addInstance(baseInstance.copy(scope));
            }
        }

        List<Reader> readers = new ArrayList<>();
        List<Processor> processors = new ArrayList<>();
        List<Writer> writers = new ArrayList<>();
        for (Instance instance : context.getInstances()) {
            Object resolved = instance.resolve();
            if (resolved instanceof Reader) {
                readers.add((Reader) resolved);
            }
            if (resolved instanceof Processor) {
                processors.add((Processor) resolved);
            }
            if (resolved instanceof Writer) {
                writers.add((Writer) resolved);
            }
        }

        ImmutableRouterConfig routerConfig = RouterConfig.builder().addAllTopology(config.topology()).build();
        StdRouter router = new StdRouter(readers, processors, writers, meterRegistry, routerConfig);
        context.addInstance(new Instance(context.getDefinition(StdRouter.NAME),
                "router", router, InjectMode.MANUAL, Priority.DEFAULT, scope));
    }
}
