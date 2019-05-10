package io.github.anycollect.extensions.loaders;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.core.manifest.ExtensionManifest;
import io.github.anycollect.core.manifest.ModuleManifest;
import io.github.anycollect.extensions.context.ExtendableContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static java.util.stream.Collectors.toList;

public final class ClassPathManifestScanDefinitionLoader implements DefinitionLoader {
    private static final Logger LOG = LoggerFactory.getLogger(ClassPathManifestScanDefinitionLoader.class);
    private static final String MANIFEST = "anycollect-manifest.yaml";
    private final ClassLoader classLoader;
    private final ObjectMapper mapper;

    public ClassPathManifestScanDefinitionLoader() {
        this.classLoader = ClassPathManifestScanDefinitionLoader.class.getClassLoader();
        this.mapper = new ObjectMapper(new YAMLFactory());
        this.mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
    }

    public ClassPathManifestScanDefinitionLoader(@Nonnull final ClassLoader classLoader,
                                                 @Nonnull final ObjectMapper mapper) {
        this.classLoader = classLoader;
        this.mapper = mapper;
    }

    @Override
    public void load(final ExtendableContext context) {
        Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(MANIFEST);
        } catch (IOException e) {
            LOG.error("could not load manifest resources from {}", classLoader, e);
            throw new ConfigurationException("could not load manifest", e);
        }
        List<Class<?>> extensions = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            ModuleManifest manifest;
            try {
                manifest = mapper.readValue(url, ModuleManifest.class);
            } catch (IOException e) {
                LOG.error("manifest {} is not valid", url, e);
                throw new ConfigurationException("manifest is not valid", e);
            }
            extensions.addAll(manifest.getExtensions().stream()
                    .map(ExtensionManifest::getClassName)
                    .map(this::loadExtensionClass)
                    .collect(toList()));
        }
        new AnnotationDefinitionLoader(extensions).load(context);
    }

    private Class<?> loadExtensionClass(final String name) {
        try {
            return classLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            LOG.error("could not load class {}", name, e);
            throw new RuntimeException();
        }
    }
}
