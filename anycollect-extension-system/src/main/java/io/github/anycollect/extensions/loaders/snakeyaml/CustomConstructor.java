package io.github.anycollect.extensions.loaders.snakeyaml;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.extensions.Definition;
import io.github.anycollect.extensions.Instance;
import io.github.anycollect.extensions.annotations.InjectMode;
import io.github.anycollect.extensions.api.JacksonModule;
import io.github.anycollect.extensions.common.expression.*;
import io.github.anycollect.extensions.common.expression.std.StdExpressionFactory;
import io.github.anycollect.extensions.context.ExtendableContext;
import io.github.anycollect.extensions.dependencies.ConfigDefinition;
import io.github.anycollect.extensions.exceptions.MissingRequiredPropertyException;
import io.github.anycollect.extensions.expression.VarSubstitutorToArgsAdapter;
import io.github.anycollect.extensions.loaders.InstanceLoader;
import io.github.anycollect.extensions.scope.Scope;
import io.github.anycollect.extensions.substitution.VarSubstitutor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

final class CustomConstructor extends Constructor {
    private static final Logger LOG = LoggerFactory.getLogger(CustomConstructor.class);
    private static final String DEPENDENCIES = "dependencies";
    private static final String EXTENSION = "extension";
    private static final String INSTANCE = "instance";
    private static final String INJECT_MODE = "injectMode";
    private final ObjectMapper mapper;
    private static final String CONFIG = "config";
    private final ExtendableContext context;
    private final Scope scope;
    private final VarSubstitutor environment;
    private final Args expressionVars;
    private final ExpressionFactory expressions;
    private final InjectableValues.Std values;
    private String extensionName;

    CustomConstructor(final ExtendableContext context,
                      final Scope scope,
                      final VarSubstitutor environment) {
        this.mapper = new ObjectMapper(new YAMLFactory());
        this.mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        this.values = new InjectableValues.Std();
        this.mapper.setInjectableValues(values);
        this.context = context;
        this.scope = scope;
        this.environment = environment;
        this.expressions = new StdExpressionFactory();
        this.expressionVars = new VarSubstitutorToArgsAdapter(environment);
        yamlConstructors.put(new Tag("!load"), new PluginInstanceDefinitionConstruct());
        yamlConstructors.put(new Tag("!ref"), new PluginRefConstruct());
        yamlConstructors.put(new Tag("!refs"), new PluginRefsConstruct());
        yamlConstructors.put(new Tag("!var"), new VarSubstituteConstruct());
        yamlConstructors.put(new Tag("!exp"), new ExpressionConstruct());
        for (Instance instance : context.getInstances()) {
            upgradeMapperIfNeeded(instance);
        }
    }

    private void upgradeMapperIfNeeded(final Instance instance) {
        Object resolved = instance.resolve();
        if (resolved instanceof Module) {
            Module module = (Module) resolved;
            LOG.debug("register jackson module {}", module.getModuleName());
            mapper.registerModule(module);
        }
        if (resolved instanceof JacksonModule) {
            Module module = ((JacksonModule) resolved).module();
            LOG.debug("register jackson module {}", module.getModuleName());
            mapper.registerModule(module);
        }
    }

    class PluginInstanceDefinitionConstruct extends AbstractConstruct {
        private MappingNode currentNode;
        private Map<Object, Object> values;

        @Override
        public Object construct(final Node node) {
            LOG.trace("start processing node: {}", node);
            if (!(node instanceof MappingNode)) {
                throw new ConfigurationException("Non-mapping use of tag !load is illegal: " + node);
            }
            this.currentNode = (MappingNode) node;
            extensionName = getExtensionName();
            this.values = constructMapping(currentNode);
            Definition definition = getExtension();
            String instanceName = getInstanceName();
            LOG.info("Creating \"{}\".{}", scope, instanceName);
            Object config = getNullableConfig(definition, instanceName);
            Map<String, Instance> singleDependencies = getSingleDependencies();
            Map<String, List<Instance>> multiDependencies = getMultiDependencies();

            Instance instance = definition.createInstance(
                    instanceName, config, singleDependencies, multiDependencies,
                    context, getInjectMode(), scope);
            context.addInstance(instance);
            Object resolved = instance.resolve();
            // TODO
            if (context.getInstance(instance.getDefinition().getExtensionPointClass(), scope) == instance) {
                CustomConstructor.this.values.addValue(instance.getDefinition().getExtensionPointClass(), resolved);
            }
            LOG.trace("instance has been successfully loaded: {}", instance);
            if (resolved instanceof InstanceLoader) {
                InstanceLoader childLoader = (InstanceLoader) resolved;
                LOG.info("Start child instance loader {}", instanceName);
                childLoader.load(context);
            }
            upgradeMapperIfNeeded(instance);
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
                        Object elementConfig = mapper.readValue(mapper.writeValueAsString(elementRawConfig),
                                config.getParameterType());
                        listConfig.add(elementConfig);
                    }
                    return listConfig;
                }
                return mapper.readValue(mapper.writeValueAsString(rawConfig), config.getParameterType());
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

    class ExpressionConstruct extends AbstractConstruct {
        @Override
        public Object construct(final Node node) {
            LOG.debug("start resolving expression: {}", node);
            if (!(node instanceof ScalarNode)) {
                throw new ConfigurationException("Non-scalar use of tag !var tag is illegal: " + node);
            }
            ScalarNode scalarNode = (ScalarNode) node;
            String exp = "\"" + scalarNode.getValue() + "\"";
            Expression expression;
            try {
                expression = expressions.create(exp);
            } catch (ParseException e) {
                LOG.error("expression {} is not valid", exp, e);
                throw new ConfigurationException("expression is not valid", e);
            }
            String resolved;
            try {
                resolved = expression.process(expressionVars);
            } catch (EvaluationException e) {
                LOG.error("expression {} could not be evaluated", exp, e);
                throw new ConfigurationException("expression could not be evaluated", e);
            }
            LOG.debug("expression {} resolved to: {}", exp, resolved);
            return resolved;
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
        if (!context.hasDefinition(extensionName)) {
            LOG.error("could not find extension definition for {}", extensionName);
            throw new ConfigurationException("could not find extension definition for " + extensionName);
        }
        return context.getDefinition(extensionName);
    }

    private Instance getInstance(final String instanceName) {
        if (!context.hasInstance(instanceName, scope)) {
            Instance instance = context.getInstance(instanceName, scope);
            if (instance == null) {
                LOG.error("could not find definition for {}, define this extension before use", instanceName);
                throw new ConfigurationException("could not find definition for " + instanceName);
            }
            return instance;
        }
        return context.getInstance(instanceName, scope);
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
