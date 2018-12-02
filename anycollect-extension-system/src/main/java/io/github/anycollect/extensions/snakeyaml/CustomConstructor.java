package io.github.anycollect.extensions.snakeyaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.anycollect.extensions.definitions.ConfigDefinition;
import io.github.anycollect.extensions.definitions.Definition;
import io.github.anycollect.extensions.definitions.Instance;
import io.github.anycollect.extensions.exceptions.ConfigurationException;
import io.github.anycollect.extensions.exceptions.MissingRequiredPropertyException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.*;

import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

final class CustomConstructor extends Constructor {
    private static final Logger LOG = LoggerFactory.getLogger(CustomConstructor.class);
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());
    private static final String DEPENDENCIES = "dependencies";
    private static final String EXTENSION = "extension";
    private static final String INSTANCE = "instance";
    private static final String CONFIG = "config";
    private final Map<String, Definition> extensionRegistry;
    private final Map<String, Instance> instanceRegistry;
    private String extensionName;

    CustomConstructor(final Collection<Definition> extensions) {
        this.extensionRegistry = new HashMap<>();
        for (Definition extension : extensions) {
            this.extensionRegistry.put(extension.getName(), extension);
        }
        this.instanceRegistry = new LinkedHashMap<>();
        yamlConstructors.put(new Tag("!load"), new PluginInstanceDefinitionConstruct());
        yamlConstructors.put(new Tag("!ref"), new PluginRefConstruct());
        yamlConstructors.put(new Tag("!refs"), new PluginRefsConstruct());
    }

    Collection<Instance> getInstances() {
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
            extensionName = getExtensionName();
            this.values = constructMapping(currentNode);
            Definition definition = getExtension();
            String instanceName = getInstanceName();
            Object config = getNullableConfig(definition, instanceName);
            Map<String, Instance> singleDependencies = getSingleDependencies();
            Map<String, List<Instance>> multiDependencies = getMultiDependencies();
            Instance instance = definition.createInstance(instanceName, config, singleDependencies, multiDependencies);
            LOG.debug("instance instance has been successfully loaded: {}", instance);
            instanceRegistry.put(instance.getInstanceName(), instance);
            return instance;
        }

        private String getInstanceName() {
            return values.containsKey(INSTANCE)
                    ? (String) values.get(INSTANCE)
                    : extensionName;
        }

        private String getExtensionName() {
            for (NodeTuple tuple : currentNode.getValue()) {
                if (EXTENSION.equals(((ScalarNode) tuple.getKeyNode()).getValue())) {
                    extensionName = ((ScalarNode) tuple.getValueNode()).getValue();
                }
            }
            if (extensionName == null) {
                LOG.error("there is no \"{}\" property in configuration in {}", EXTENSION, currentNode);
                throw new MissingRequiredPropertyException(EXTENSION);
            }
            return extensionName;
        }

        private Map<String, Instance> getSingleDependencies() {
            if (values.containsKey(DEPENDENCIES)) {
                return ((Map<?, ?>) (values.get(DEPENDENCIES))).entrySet().stream()
                        .filter(entry -> entry.getValue() instanceof Instance)
                        .map(entry -> new InstanceBinding((String) entry.getKey(), (Instance) entry.getValue()))
                        .collect(toMap(InstanceBinding::getName, InstanceBinding::getInstance));
            }
            return Collections.emptyMap();
        }

        @SuppressWarnings("unchecked")
        private Map<String, List<Instance>> getMultiDependencies() {
            if (values.containsKey(DEPENDENCIES)) {
                return ((Map<?, ?>) (values.get(DEPENDENCIES))).entrySet().stream()
                        .filter(entry -> entry.getValue() instanceof List)
                        .map(entry -> new InstancesBinding((String) entry.getKey(), (List<Instance>) entry.getValue()))
                        .collect(toMap(InstancesBinding::getName, InstancesBinding::getInstances));
            }
            return Collections.emptyMap();
        }

        private Object getNullableConfig(final Definition definition, final String instanceName) {
            Object rawConfig = values.get(CONFIG);
            if (rawConfig == null) {
                return null;
            }
            ConfigDefinition config = definition.getConfigDefinition().orElseThrow(() -> {
                LOG.error("extension {} is not configurable, found config: {}", definition, rawConfig);
                return new ConfigurationException("custom config for " + definition + " is not supported");
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
        public Instance construct(final Node node) {
            LOG.debug("start resolving instance dependency definition: {}", node);
            if (!(node instanceof ScalarNode)) {
                throw new ConfigurationException("Non-scalar use of tag !ref tag is illegal: " + node);
            }
            ScalarNode scalarNode = (ScalarNode) node;
            String instanceName = scalarNode.getValue();
            Instance instance = getInstance(instanceName);
            LOG.debug("instance dependency definition has been successfully resolved: {}", instance);
            return instance;
        }
    }

    class PluginRefsConstruct extends AbstractConstruct {
        @Override
        public List<Instance> construct(final Node node) {
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
            List<Instance> instances = new ArrayList<>();
            for (String dependencyName : dependencyNames) {
                instances.add(getInstance(dependencyName));
            }
            LOG.debug("instance dependencies definition has been successfully resolved: {}", instances);
            return instances;
        }
    }

    private Definition getExtension() {
        if (!extensionRegistry.containsKey(extensionName)) {
            LOG.error("could not find extension definition for {}", extensionName);
            throw new ConfigurationException("could not find extension definition for " + extensionName);
        }
        return extensionRegistry.get(extensionName);
    }

    private Instance getInstance(final String instanceName) {
        if (!instanceRegistry.containsKey(instanceName)) {
            LOG.error("could not find definition for {}, define this extension before use", instanceName);
            throw new ConfigurationException("could not find definition for " + instanceName);
        }
        return instanceRegistry.get(instanceName);
    }

    @Getter
    private static final class InstanceBinding {
        private final String name;
        private final Instance instance;

        private InstanceBinding(final String name, final Instance instance) {
            this.name = name;
            this.instance = instance;
        }
    }

    @Getter
    private static final class InstancesBinding {
        private final String name;
        private final List<Instance> instances;

        private InstancesBinding(final String name, final List<Instance> instances) {
            this.name = name;
            this.instances = instances;
        }
    }
}
