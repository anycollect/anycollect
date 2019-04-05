package io.github.anycollect.extensions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.core.manifest.ExtensionManifest;
import io.github.anycollect.core.manifest.ModuleManifest;
import io.github.anycollect.extensions.definitions.Definition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import static java.util.stream.Collectors.toList;

public final class ClassLoaderDefinitionLoader implements DefinitionLoader {
    private static final Logger LOG = LoggerFactory.getLogger(ClassLoaderDefinitionLoader.class);
    private static final String MANIFEST = "anycollect-manifest.yaml";
    private final ClassLoader classLoader;
    private final ObjectMapper mapper;

    public ClassLoaderDefinitionLoader(@Nonnull final ClassLoader classLoader, @Nonnull final ObjectMapper mapper) {
        this.classLoader = classLoader;
        this.mapper = mapper;
    }

    @Override
    public Collection<Definition> load() {
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
            ModuleManifest manifest = null;
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
        return new AnnotationDefinitionLoader(extensions).load();
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
