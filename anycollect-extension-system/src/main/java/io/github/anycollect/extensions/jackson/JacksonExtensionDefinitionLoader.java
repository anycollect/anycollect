package io.github.anycollect.extensions.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.anycollect.extensions.ExtensionDefinitionLoader;
import io.github.anycollect.extensions.definitions.ExtensionDefinition;
import io.github.anycollect.extensions.exceptions.ExtensionNotFoundException;
import io.github.anycollect.extensions.exceptions.MissingRequiredPropertyException;
import io.github.anycollect.extensions.exceptions.WrongExtensionClassException;
import io.github.anycollect.extensions.exceptions.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class JacksonExtensionDefinitionLoader implements ExtensionDefinitionLoader {
    private static final Logger LOG = LoggerFactory.getLogger(JacksonExtensionDefinitionLoader.class);
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    @Override
    public List<ExtensionDefinition> load(final Reader reader) {
        LOG.debug("start to load extension definitions from {}", reader);
        List<Map<String, String>> rawDefinitions;
        try {
            rawDefinitions = MAPPER.readValue(reader, new TypeReference<List<Map<String, String>>>() { });
        } catch (IOException e) {
            LOG.error("unexpected error during reading configuration from {}", reader, e);
            throw new ConfigurationException(
                    String.format("unexpected error during reading configuration from %s", reader), e);
        }
        List<ExtensionDefinition> definitions = new ArrayList<>();

        for (Map<String, String> rawDefinition : rawDefinitions) {
            ExtensionDefinition definition = parseMap(rawDefinition);
            definitions.add(definition);
        }
        LOG.debug("extension definitions has been successfully loaded, {}", definitions);
        return definitions;
    }

    @SuppressWarnings("unchecked")
    private ExtensionDefinition parseMap(final Map<String, String> map) {
        String extensionName = getRequiredString("extensionName", map);
        String extensionPointClassName = getRequiredString("extensionPointClassName", map);
        String extensionClassName = getRequiredString("extensionClassName", map);
        String configClassName = getOptionalString("configClassName", map);

        Class extensionPointClass = loadClass(extensionPointClassName);
        Class extensionClass = loadClass(extensionClassName);

        if (!extensionPointClass.isAssignableFrom(extensionClass)) {
            LOG.error("extension {} must implement extension point {}",
                    extensionClassName, extensionPointClassName);
            throw new WrongExtensionClassException(extensionPointClass, extensionClass);
        }

        Class<?> configClass = configClassName != null ? loadClass(configClassName) : null;

        return ExtensionDefinition.builder()
                .withName(extensionName)
                .withExtension(extensionPointClass, extensionClass)
                .withConfig(configClass)
                .build();
    }

    private String getOptionalString(final String key, final Map<String, String> map) {
        return map.get(key);
    }

    private String getRequiredString(final String key, final Map<String, String> map)
            throws MissingRequiredPropertyException {
        String value = map.get(key);
        if (value == null) {
            LOG.error("required property \"{}\" is missed in configuration: {}", key, map);
            throw new MissingRequiredPropertyException(key);
        }
        return value;
    }

    private Class loadClass(final String name) throws ExtensionNotFoundException {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            LOG.error("class specified in extension meta info: \"{}\" is not found in classpath", name);
            throw new ExtensionNotFoundException(name);
        }
    }
}
