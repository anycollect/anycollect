package io.github.anycollect.extensions.snakeyaml;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.extensions.VarSubstitutor;
import io.github.anycollect.extensions.definitions.*;
import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.extensions.exceptions.MissingRequiredPropertyException;
import io.github.anycollect.jackson.AnyCollectModule;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.noop.NoopMeterRegistry;
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
    private static final String INJECT_MODE = "injectMode";
    private static final String SCOPE = "scope";
    private static final String PRIORITY = "priority";
    private static final String CONFIG = "config";
    private final Map<String, Definition> extensionRegistry;
    private final Map<String, Instance> instanceRegistry;
    private final ExtendableContext context;
    private final String scopeId;
    private final VarSubstitutor environment;
    private static final InjectableValues.Std VALUES;
    private String extensionName;

    static {
        MAPPER.registerModule(new AnyCollectModule());
        MAPPER.registerModule(new GuavaModule());
        VALUES = new InjectableValues.Std();
        VALUES.addValue(Clock.class, Clock.getDefault());
        VALUES.addValue(MeterRegistry.class, new NoopMeterRegistry());
        MAPPER.setInjectableValues(VALUES);
    }

    CustomConstructor(final Collection<Definition> extensions) {
        this(new ContextImpl(), "default", extensions, VarSubstitutor.EMPTY);
    }

    CustomConstructor(final ExtendableContext context,
                      final String scopeId,
                      final Collection<Definition> extensions,
                      final VarSubstitutor environment) {
        this.extensionRegistry = new HashMap<>();
        for (Definition extension : extensions) {
            this.extensionRegistry.put(extension.getName(), extension);
        }
        this.instanceRegistry = new LinkedHashMap<>();
        this.context = context;
        this.scopeId = scopeId;
        this.environment = environment;
        yamlConstructors.put(new Tag("!load"), new PluginInstanceDefinitionConstruct());
        yamlConstructors.put(new Tag("!ref"), new PluginRefConstruct());
        yamlConstructors.put(new Tag("!refs"), new PluginRefsConstruct());
        yamlConstructors.put(new Tag("!var"), new VarSubstituteConstruct());
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
            Instance instance = definition.createInstance(
                    instanceName, config, singleDependencies, multiDependencies,
                    context, getInjectMode(), getScope(), getPriority(), scopeId);
            context.addInstance(instance);
            Object resolved = instance.resolve();
            // TODO
            if (context.getInstance(instance.getDefinition().getExtensionPointClass(), scopeId) == instance) {
                VALUES.addValue(instance.getDefinition().getExtensionPointClass(), resolved);
            }
            LOG.debug("instance has been successfully loaded: {}", instance);
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

        private InjectMode getInjectMode() {
            return values.containsKey(INJECT_MODE)
                    ? InjectMode.valueOf(((String) values.get(INJECT_MODE)).toUpperCase())
                    : InjectMode.MANUAL;
        }

        private Scope getScope() {
            return values.containsKey(SCOPE)
                    ? Scope.valueOf(((String) values.get(SCOPE)).toUpperCase())
                    : Scope.LOCAL;
        }

        private Priority getPriority() {
            return values.containsKey(PRIORITY)
                    ? Priority.valueOf(((String) values.get(PRIORITY)).toUpperCase())
                    : Priority.DEFAULT;
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
                if (!definition.getConfigDefinition().map(ConfigDefinition::isSingle).orElse(true)) {
                    return Collections.emptyList();
                }
                return null;
            }
            ConfigDefinition config = definition.getConfigDefinition().orElseThrow(() -> {
                LOG.error("extension {} is not configurable", definition);
                return new ConfigurationException("custom config for " + definition + " is not supported");
            });
            if (!config.getConfigKey().isEmpty()) {
                rawConfig = ((Map<?, ?>) rawConfig).get(config.getConfigKey());
            }
            if (rawConfig == null) {
                return null;
            }
            try {
                if (!(config.isSingle())) {
                    if (!(rawConfig instanceof List)) {
                        throw new IllegalArgumentException("TODO");
                    }
                    List<Object> listConfig = new ArrayList<>();
                    List<?> listRawConfig = (List<?>) rawConfig;
                    for (Object elementRawConfig : listRawConfig) {
                        Object elementConfig = MAPPER.readValue(MAPPER.writeValueAsString(elementRawConfig),
                                config.getParameterType());
                        listConfig.add(elementConfig);
                    }
                    return listConfig;
                }
                return MAPPER.readValue(MAPPER.writeValueAsString(rawConfig), config.getParameterType());
            } catch (IOException e) {
                LOG.error("unexpected error during parsing configuration of class {} for {}, config: {}",
                        config.getParameterType().getName(), instanceName, rawConfig, e);
                throw new ConfigurationException("unexpected error during parsing configuration", e);
            }
        }
    }

    class VarSubstituteConstruct extends AbstractConstruct {
        @Override
        public Object construct(final Node node) {
            LOG.debug("start resolving environment variable: {}", node);
            if (!(node instanceof ScalarNode)) {
                throw new ConfigurationException("Non-scalar use of tag !var tag is illegal: " + node);
            }
            ScalarNode scalarNode = (ScalarNode) node;
            String varName = scalarNode.getValue();
            Object var = environment.substitute(varName);
            LOG.debug("environment variable {} resolved to: {}", varName, var);
            return var;
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
            Instance instance = context.getInstance(instanceName, scopeId);
            if (instance == null) {
                LOG.error("could not find definition for {}, define this extension before use", instanceName);
                throw new ConfigurationException("could not find definition for " + instanceName);
            }
            return instance;
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
