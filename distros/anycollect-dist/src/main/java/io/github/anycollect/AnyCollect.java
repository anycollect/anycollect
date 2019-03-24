package io.github.anycollect;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.anycollect.core.api.Router;
import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.manifest.ExtensionManifest;
import io.github.anycollect.core.manifest.ModuleManifest;
import io.github.anycollect.extensions.AnnotationDefinitionLoader;
import io.github.anycollect.extensions.EnvVarSubstitutor;
import io.github.anycollect.extensions.InstanceLoader;
import io.github.anycollect.extensions.VarSubstitutor;
import io.github.anycollect.extensions.definitions.*;
import io.github.anycollect.extensions.snakeyaml.YamlInstanceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static java.util.stream.Collectors.toList;

public final class AnyCollect {
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());
    private static final long PAUSE = 5000;
    private static final Logger LOG = LoggerFactory.getLogger(AnyCollect.class);

    static {
        MAPPER.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
    }

    private final Collection<Instance> instances;

    private AnyCollect(final File configFile) throws Exception {
        List<Class<?>> extensionClasses = loadExtensionClasses();
        AnnotationDefinitionLoader annotationDefinitionLoader = new AnnotationDefinitionLoader(extensionClasses);
        Collection<Definition> definitions = annotationDefinitionLoader.load();
        VarSubstitutor substitutor = new EnvVarSubstitutor();
        ExtendableContext context = new ContextImpl(definitions);
        Scope scope = FileScope.root(configFile);
        InstanceLoader instanceLoader
                = new YamlInstanceLoader(scope, new FileReader(configFile), substitutor);
        Instance rootLoader = new Instance(context.getDefinition(YamlInstanceLoader.NAME),
                "root", instanceLoader, InjectMode.AUTO, Priority.DEFAULT, scope);
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
            Runtime.getRuntime().addShutdownHook(new Thread(AnyCollect.this::shutdown));
            while (true) {
                Thread.sleep(PAUSE);
                LOG.info("up");
            }
        }
    }

    public static void main(final String... args) throws Exception {
        File config = new File(args[0]);
        new AnyCollect(config).run();
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

    private void shutdown() {
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

    private static List<Class<?>> loadExtensionClasses() throws IOException {
        Enumeration<URL> resources = AnyCollect.class.getClassLoader().getResources("anycollect-manifest.yaml");
        List<Class<?>> extensions = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            ModuleManifest manifest = MAPPER.readValue(url, ModuleManifest.class);
            extensions.addAll(manifest.getExtensions().stream()
                    .map(ExtensionManifest::getClassName)
                    .map(AnyCollect::loadExtensionClass)
                    .collect(toList()));
        }
        return extensions;
    }

    private static Class<?> loadExtensionClass(final String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            LOG.error("could not load class {}", name, e);
            throw new RuntimeException();
        }
    }
}
