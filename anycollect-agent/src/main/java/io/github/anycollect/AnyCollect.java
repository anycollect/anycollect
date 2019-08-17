package io.github.anycollect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.anycollect.core.api.Router;
import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.extensions.Instance;
import io.github.anycollect.extensions.annotations.InjectMode;
import io.github.anycollect.extensions.context.ContextImpl;
import io.github.anycollect.extensions.context.ExtendableContext;
import io.github.anycollect.extensions.loaders.ClassPathManifestScanDefinitionLoader;
import io.github.anycollect.extensions.loaders.DefinitionLoader;
import io.github.anycollect.extensions.loaders.InstanceLoader;
import io.github.anycollect.extensions.loaders.snakeyaml.YamlInstanceLoader;
import io.github.anycollect.extensions.scope.FileScope;
import io.github.anycollect.extensions.scope.Scope;
import io.github.anycollect.extensions.scope.SimpleScope;
import io.github.anycollect.extensions.substitution.VarSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class AnyCollect implements Runnable {
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());
    private static final long PAUSE = 5000;
    private static final Logger LOG = LoggerFactory.getLogger(AnyCollect.class);
    private final Collection<Instance> instances;

    public AnyCollect(final File configFile, final VarSubstitutor substitutor) throws IOException {
        this(configFile, Collections.emptyList(), substitutor);
    }

    public AnyCollect(@Nullable final File configFile, final List<String> classpathConfigFiles, final VarSubstitutor substitutor) throws IOException {
        DefinitionLoader loader = new ClassPathManifestScanDefinitionLoader(AnyCollect.class.getClassLoader(), MAPPER);
        ExtendableContext context = new ContextImpl();
        loader.load(context);
        List<Reader> readers = new ArrayList<>();
        if (configFile != null) {
            readers.add(new FileReader(configFile));
        }
        for (String classpathConfigFile : classpathConfigFiles) {
            readers.add(new InputStreamReader(this.getClass().getClassLoader().getResource(classpathConfigFile).openStream()));
        }
        Scope scope;
        if (configFile != null) {
            scope = FileScope.root(configFile);
        } else {
            scope = new SimpleScope(null, "root");
        }
        for (Reader reader : readers) {
            // TODO add var substitutor
            //        context.addInstance(substitutor);

            InstanceLoader instanceLoader
                    = new YamlInstanceLoader(scope, reader, substitutor);
            Instance rootLoader = new Instance(context.getDefinition(YamlInstanceLoader.NAME),
                    "root", instanceLoader, InjectMode.AUTO, scope);
            context.addInstance(rootLoader);
            instanceLoader.load(context);
        }
        for (Reader reader : readers) {
            reader.close();
        }
        this.instances = new ArrayList<>(context.getInstances());
    }

    @Override
    public void run() {
        Router router = null;
        for (Instance instance : instances) {
            Object extension = instance.resolve();
            if (extension instanceof Router) {
                router = (Router) extension;
            }
        }
        if (router == null) {
            LOG.error("no router found");
        } else {
            initialize();
            router.start();
            while (true) {
                try {
                    Thread.sleep(PAUSE);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                LOG.info("up");
            }
        }
    }

    private void initialize() {
        for (Instance instance : instances) {
            if (!instance.isCopy()) {
                Object resolved = instance.resolve();
                if (resolved instanceof Lifecycle) {
                    ((Lifecycle) resolved).init();
                }
            }
        }
    }

    public void shutdown() {
        destroy();
    }

    private void destroy() {
        ArrayList<Instance> reversed = new ArrayList<>(this.instances);
        Collections.reverse(reversed);
        for (Instance instance : reversed) {
            if (!instance.isCopy()) {
                Object resolved = instance.resolve();
                if (resolved instanceof Lifecycle) {
                    ((Lifecycle) resolved).destroy();
                }
            }
        }
    }
}
