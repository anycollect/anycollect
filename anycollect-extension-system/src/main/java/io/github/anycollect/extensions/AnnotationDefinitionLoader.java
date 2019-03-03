package io.github.anycollect.extensions;

import io.github.anycollect.extensions.annotations.*;
import io.github.anycollect.extensions.definitions.*;
import io.github.anycollect.extensions.exceptions.ConfigurationException;
import io.github.anycollect.extensions.exceptions.ExtensionDescriptorException;
import io.github.anycollect.extensions.exceptions.UnresolvableConstructorException;
import io.github.anycollect.extensions.exceptions.WrongExtensionMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import static java.util.stream.Collectors.joining;

public final class AnnotationDefinitionLoader implements DefinitionLoader {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotationDefinitionLoader.class);
    private final List<Class<?>> extensionClasses;

    public AnnotationDefinitionLoader(final List<Class<?>> extensionClassNames) {
        this.extensionClasses = new ArrayList<>(extensionClassNames);
    }

    @Override
    public Collection<Definition> load() {
        LOG.debug("start to load extension definitions from {}", extensionClasses);
        List<Definition> definitions = new ArrayList<>();
        for (Class<?> extensionClass : extensionClasses) {
            Definition definition = parse(extensionClass);
            definitions.add(definition);
        }
        LOG.debug("extension definitions has been successfully loaded, {}", definitions);
        return definitions;
    }

    @SuppressWarnings("unchecked")
    private Definition parse(final Class<?> extensionClass) {
        validateExtensionClass(extensionClass);
        Class extensionPointClass = loadExtensionPointClass(extensionClass);
        String extensionName = loadExtensionName(extensionClass);

        if (!extensionPointClass.isAssignableFrom(extensionClass)) {
            LOG.error("extension {} must implement extension point {}",
                    extensionClass, extensionPointClass);
            throw new WrongExtensionMappingException(extensionPointClass, extensionClass);
        }
        Constructor<?> constructor = resolveExtensionConstructor(extensionClass);
        List<AnnotatedParameter<ExtConfig>> configs = findParameterWithAnnotation(constructor, ExtConfig.class);
        if (configs.size() > 1) {
            errorConfig(extensionClass);
        }
        ConfigDefinition config = null;
        if (!configs.isEmpty()) {
            AnnotatedParameter<ExtConfig> configParameter = configs.get(0);
            if (List.class.isAssignableFrom(configParameter.type)) {
                config = new ConfigDefinition(
                        configParameter.annotation.key(),
                        resolveCollectionGeneric(constructor, configParameter),
                        configParameter.annotation.optional(),
                        configParameter.position,
                        false);
            } else {
                config = new ConfigDefinition(
                        configParameter.annotation.key(),
                        configParameter.type,
                        configParameter.annotation.optional(),
                        configParameter.position,
                        true);
            }
        }
        List<AnnotatedParameter<ExtDependency>> dependencyParams =
                findParameterWithAnnotation(constructor, ExtDependency.class);
        List<AnnotatedParameter<InstanceId>> instanceIds = findParameterWithAnnotation(constructor, InstanceId.class);
        List<SingleDependencyDefinition> singleDependencyDefinitions = new ArrayList<>();
        List<MultiDependencyDefinition> multiDependencyDefinitions = new ArrayList<>();

        for (AnnotatedParameter<InstanceId> instanceId : instanceIds) {
            if (!instanceId.type.equals(String.class)) {
                throw new UnresolvableConstructorException(extensionClass, constructor, instanceId.type);
            }
            singleDependencyDefinitions.add(new SingleDependencyDefinition(
                            "__instanceId__",
                            instanceId.type,
                            false,
                            instanceId.position
                    )
            );
        }
        for (AnnotatedParameter<ExtDependency> param : dependencyParams) {
            if (Collection.class.isAssignableFrom(param.type)) {
                if (!param.type.equals(List.class)) {
                    LOG.error("for multiple dependency {} must be used, found: {} in {}",
                            List.class, param.type, extensionClass.getName());
                    throw new UnresolvableConstructorException(extensionClass, constructor, param.type);
                }
                multiDependencyDefinitions.add(new MultiDependencyDefinition(
                        param.annotation.qualifier(),
                        resolveCollectionGeneric(constructor, param),
                        param.annotation.optional(),
                        param.position)
                );
            } else {
                singleDependencyDefinitions.add(new SingleDependencyDefinition(
                        param.annotation.qualifier(), param.type, param.annotation.optional(), param.position));
            }
        }
        List<AbstractDependencyDefinition> dependencies = new ArrayList<>();
        if (config != null) {
            dependencies.add(config);
        }
        dependencies.addAll(singleDependencyDefinitions);
        dependencies.addAll(multiDependencyDefinitions);
        validateConstrictor(extensionClass, constructor, dependencies);
        return Definition.builder()
                .withName(extensionName)
                .withExtension(extensionPointClass, constructor)
                .withSingleDependencies(singleDependencyDefinitions)
                .withMultiDependencies(multiDependencyDefinitions)
                .withConfig(config)
                .build();
    }

    private Class<?> resolveCollectionGeneric(final Constructor<?> constructor,
                                              final AnnotatedParameter<?> param) {
        ParameterizedType type = (ParameterizedType) constructor.getGenericParameterTypes()[param.position];
        return (Class<?>) type.getActualTypeArguments()[0];
    }

    private void validateConstrictor(final Class extensionClass,
                                     final Constructor<?> constructor,
                                     final List<AbstractDependencyDefinition> parameters) {
        Set<Integer> unresolvedParametersNumbers = new HashSet<>();
        for (int number = 0; number < constructor.getParameterCount(); ++number) {
            unresolvedParametersNumbers.add(number);
        }
        for (AbstractDependencyDefinition parameter : parameters) {
            unresolvedParametersNumbers.remove(parameter.getPosition());
        }
        if (unresolvedParametersNumbers.size() > 0) {
            String params = unresolvedParametersNumbers.stream()
                    .map(number -> constructor.getParameterTypes()[number])
                    .map(Class::getSimpleName)
                    .collect(joining(", "));
            LOG.error("cannot resolve parameters {} in constructor {} of {} extension",
                    params, constructor, extensionClass.getSimpleName());
            throw new UnresolvableConstructorException(extensionClass, constructor, unresolvedParametersNumbers);
        }
    }

    private <T extends Annotation> List<AnnotatedParameter<T>> findParameterWithAnnotation(
            final Constructor<?> constructor, final Class<T> annotationClass) {
        Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        int parameterCount = constructor.getParameterCount();
        List<AnnotatedParameter<T>> parameters = new ArrayList<>();
        for (int parameterNumber = 0; parameterNumber < parameterCount; ++parameterNumber) {
            Class<?> parameterClass = parameterTypes[parameterNumber];
            for (Annotation annotation : parameterAnnotations[parameterNumber]) {
                if (annotationClass.isAssignableFrom(annotation.getClass())) {
                    @SuppressWarnings("unchecked")
                    T wantedAnnotation = (T) annotation;
                    parameters.add(new AnnotatedParameter<>(parameterClass, wantedAnnotation, parameterNumber));
                }
            }
        }
        return parameters;
    }

    private Constructor<?> resolveExtensionConstructor(final Class<?> extensionClass) {
        Constructor<?> targetConstructor = null;
        for (Constructor constructor : extensionClass.getConstructors()) {
            if (constructor.isAnnotationPresent(ExtCreator.class)) {
                if (targetConstructor == null) {
                    targetConstructor = constructor;
                } else {
                    errorConstructor(extensionClass);
                }
            }
        }
        if (targetConstructor == null || !Modifier.isPublic(targetConstructor.getModifiers())) {
            errorConstructor(extensionClass);
        }
        return targetConstructor;
    }

    private void errorConstructor(final Class<?> extensionClass) {
        LOG.error("extension {} must have exactly one constructor annotated {} annotation "
                        + "and this constructor must be public",
                extensionClass.getName(), ExtCreator.class.getName());
        throw new ExtensionDescriptorException(
                String.format("extension %s must have exactly one constructor annotated %s annotation",
                        extensionClass.getName(), ExtCreator.class.getName()));
    }

    private void errorConfig(final Class<?> extensionClass) {
        LOG.error("extension {} must have at most one parameter annotated {} annotation in constrictor",
                extensionClass.getName(), ExtConfig.class.getName());
        throw new ExtensionDescriptorException(
                String.format("extension %s must have at most one parameter annotated %s annotation in constructor",
                        extensionClass.getName(), ExtConfig.class.getName()));
    }

    private void validateExtensionClass(final Class<?> extClass) {
        Extension extension = extClass.getAnnotation(Extension.class);
        if (extension == null) {
            LOG.error("extension class {} must have {} annotation on the class declaration",
                    extClass.getName(), Extension.class.getName());
            throw new ConfigurationException("extension class " + extClass.getName() + " must have "
                    + Extension.class.getName() + " annotation");
        }
    }

    private String loadExtensionName(final Class extClass) {
        Extension extension = (Extension) extClass.getAnnotation(Extension.class);
        return extension.name();
    }

    private Class loadExtensionPointClass(final Class extClass) {
        Extension extension = (Extension) extClass.getAnnotation(Extension.class);
        return extension.point();
    }

    private static class AnnotatedParameter<T extends Annotation> {
        private final Class<?> type;
        private final T annotation;
        private final int position;

        AnnotatedParameter(final Class<?> type, final T annotation, final int serialNumber) {
            this.type = type;
            this.annotation = annotation;
            this.position = serialNumber;
        }
    }
}
