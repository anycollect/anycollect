package io.github.anycollect;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.anycollect.core.api.Router;
import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.extensions.Definition;
import io.github.anycollect.extensions.Instance;
import io.github.anycollect.extensions.loaders.ClassLoaderDefinitionLoader;
import io.github.anycollect.extensions.loaders.DefinitionLoader;
import io.github.anycollect.extensions.loaders.InstanceLoader;
import io.github.anycollect.extensions.substitution.VarSubstitutor;
import io.github.anycollect.extensions.context.ContextImpl;
import io.github.anycollect.extensions.context.ExtendableContext;
import io.github.anycollect.extensions.scope.FileScope;
import io.github.anycollect.extensions.scope.Scope;
import io.github.anycollect.extensions.loaders.snakeyaml.YamlInstanceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public final class AnyCollect {
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());
    private static final long PAUSE = 5000;
    private static final Logger LOG = LoggerFactory.getLogger(AnyCollect.class);
    private final Collection<Instance> instances;

    static {
        MAPPER.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
    }

    public AnyCollect(final File configFile, final VarSubstitutor substitutor)
            throws Exception {
        DefinitionLoader loader = new ClassLoaderDefinitionLoader(AnyCollect.class.getClassLoader(), MAPPER);
        Collection<Definition> definitions = loader.load();
        ExtendableContext context = new ContextImpl(definitions);
        Scope scope = FileScope.root(configFile);

        InstanceLoader instanceLoader
                = new YamlInstanceLoader(scope, new FileReader(configFile), substitutor);
        Instance rootLoader = new Instance(context.getDefinition(YamlInstanceLoader.NAME),
                "root", instanceLoader, Instance.InjectMode.AUTO, scope);
        context.addInstance(rootLoader);
        instanceLoader.load(context);
        this.instances = new ArrayList<>(context.getInstances());
    }

    public void run() throws Exception {
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
                Thread.sleep(PAUSE);
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
        Thread.currentThread().setName("graceful-shutdown");
        LOG.info("graceful shutdown");
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
