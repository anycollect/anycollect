package io.github.anycollect.extensions.snakeyaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.anycollect.extensions.definitions.ConfigParameterDefinition;
import io.github.anycollect.extensions.definitions.ExtensionDefinition;
import io.github.anycollect.extensions.definitions.ExtensionInstanceDependencyDefinition;
import io.github.anycollect.extensions.definitions.ExtensionInstanceDefinition;
import io.github.anycollect.extensions.exceptions.ConfigurationException;
import io.github.anycollect.extensions.exceptions.MissingRequiredPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.*;

import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toList;

final class CustomConstructor extends Constructor {
    private static final Logger LOG = LoggerFactory.getLogger(CustomConstructor.class);
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());
    private static final String DEPENDENCIES = "dependencies";
    private static final String EXTENSION = "extension";
    private static final String INSTANCE = "instance";
    private static final String CONFIG = "config";
    private final Map<String, ExtensionDefinition> extensionRegistry;
    private final Map<String, ExtensionInstanceDefinition> instanceRegistry;

    CustomConstructor(final Collection<ExtensionDefinition> extensions) {
        this.extensionRegistry = new HashMap<>();
        for (ExtensionDefinition extension : extensions) {
            this.extensionRegistry.put(extension.getName(), extension);
        }
        this.instanceRegistry = new LinkedHashMap<>();
        yamlConstructors.put(new Tag("!load"), new PluginInstanceDefinitionConstruct());
        yamlConstructors.put(new Tag("!ref"), new PluginRefConstruct());
        yamlConstructors.put(new Tag("!refs"), new PluginRefsConstruct());
    }

    Collection<ExtensionInstanceDefinition> getInstances() {
        return Collections.unmodifiableCollection(instanceRegistry.values());
    }

    class PluginInstanceDefinitionConstruct extends AbstractConstruct {
        private MappingNode currentNode;
        private Map<Object, Object> values;

        @Override
        public Object construct(final Node node) {
            LOG.debug("start processing node: {}", node);
            if (!(node instanceof MappingNode)) {
                throw new ConfigurationException("Non-mapping use of tag !load is illegal: " + node);
            }
            this.currentNode = (MappingNode) node;
            this.values = constructMapping(currentNode);
            String extensionName = getExtensionName();
            ExtensionDefinition extensionDefinition = getExtension(extensionName);
            String instanceName = getInstanceName(extensionName);
            Object config = getNullableConfig(extensionDefinition, instanceName);
            Map<String, ExtensionInstanceDependencyDefinition> dependencies = getDependencies();
            ExtensionInstanceDefinition definition =
                    new ExtensionInstanceDefinition(extensionDefinition, instanceName, config, dependencies);
            LOG.debug("instance definition has been successfully loaded: {}", definition);
            instanceRegistry.put(definition.getInstanceName(), definition);
            return definition;
        }

        private String getInstanceName(final String extensionName) {
            return values.containsKey(INSTANCE)
                    ? (String) values.get(INSTANCE)
                    : extensionName;
        }

        private String getExtensionName() {
            final String extensionName = (String) values.get(EXTENSION);
            if (extensionName == null) {
                LOG.error("there is no \"{}\" property in configuration in {}", EXTENSION, currentNode);
                throw new MissingRequiredPropertyException(EXTENSION);
            }
            return extensionName;
        }

        @SuppressWarnings("unchecked")
        private Map<String, ExtensionInstanceDependencyDefinition> getDependencies() {
            return values.containsKey(DEPENDENCIES)
                    ? (Map<String, ExtensionInstanceDependencyDefinition>) values.get(DEPENDENCIES)
                    : Collections.emptyMap();
        }

        private Object getNullableConfig(final ExtensionDefinition extensionDefinition, final String instanceName) {
            Object rawConfig = values.get(CONFIG);
            if (rawConfig == null) {
                return null;
            }
            ConfigParameterDefinition config = extensionDefinition.getConfig().orElseThrow(() -> {
                LOG.error("extension {} is not configurable, found config: {}", extensionDefinition, rawConfig);
                return new ConfigurationException("custom config for " + extensionDefinition + " is not supported");
            });
            try {
                return MAPPER.readValue(MAPPER.writeValueAsString(rawConfig), config.getParameterType());
            } catch (IOException e) {
                LOG.error("unexpected error during parsing configuration of class {} for {}, config: {}",
                        config.getParameterType().getName(), instanceName, rawConfig, e);
                throw new ConfigurationException("unexpected error during parsing configuration", e);
            }
        }
    }

    class PluginRefConstruct extends AbstractConstruct {
        @Override
        public ExtensionInstanceDependencyDefinition construct(final Node node) {
            LOG.debug("start resolving instance dependency definition: {}", node);
            if (!(node instanceof ScalarNode)) {
                throw new ConfigurationException("Non-scalar use of tag !ref tag is illegal: " + node);
            }
            ScalarNode scalarNode = (ScalarNode) node;
            String instanceName = scalarNode.getValue();
            ExtensionInstanceDependencyDefinition definition =
                    new ExtensionInstanceDependencyDefinition(getInstance(instanceName));
            LOG.debug("instance dependency definition has been successfully resolved: {}", definition);
            return definition;
        }
    }

    class PluginRefsConstruct extends AbstractConstruct {
        @Override
        public ExtensionInstanceDependencyDefinition construct(final Node node) {
            LOG.debug("start resolving instance dependencies definition: {}", node);
            if (!(node instanceof SequenceNode)) {
                throw new ConfigurationException("Non-sequence use of tag !refs tag is illegal: " + node);
            }
            SequenceNode sequenceNode = (SequenceNode) node;
            List<Node> values = sequenceNode.getValue();
            List<String> dependencyNames = values.stream()
                    .map(n -> ((ScalarNode) n).getValue())
                    .map(String::trim)
                    .collect(toList());
            List<ExtensionInstanceDefinition> definitions = new ArrayList<>();
            for (String dependencyName : dependencyNames) {
                definitions.add(getInstance(dependencyName));
            }
            ExtensionInstanceDependencyDefinition definition = new ExtensionInstanceDependencyDefinition(definitions);
            LOG.debug("instance dependencies definition has been successfully resolved: {}", definition);
            return definition;
        }
    }

    private ExtensionDefinition getExtension(final String extensionName) {
        if (!extensionRegistry.containsKey(extensionName)) {
            LOG.error("could not find extension definition for {}", extensionName);
            throw new ConfigurationException("could not find extension definition for " + extensionName);
        }
        return extensionRegistry.get(extensionName);
    }

    private ExtensionInstanceDefinition getInstance(final String instanceName) {
        if (!instanceRegistry.containsKey(instanceName)) {
            LOG.error("could not find definition for {}, define this extension before use", instanceName);
            throw new ConfigurationException("could not find definition for " + instanceName);
        }
        return instanceRegistry.get(instanceName);
    }
}
