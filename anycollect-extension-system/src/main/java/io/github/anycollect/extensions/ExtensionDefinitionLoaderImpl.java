package io.github.anycollect.extensions;

import io.github.anycollect.extensions.annotations.*;
import io.github.anycollect.extensions.definitions.AbstractExtensionParameterDefinition;
import io.github.anycollect.extensions.definitions.ConfigParameterDefinition;
import io.github.anycollect.extensions.definitions.ExtensionDefinition;
import io.github.anycollect.extensions.definitions.ExtensionDependencyDefinition;
import io.github.anycollect.extensions.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import static java.util.stream.Collectors.joining;

public final class ExtensionDefinitionLoaderImpl implements ExtensionDefinitionLoader {
    private static final Logger LOG = LoggerFactory.getLogger(ExtensionDefinitionLoaderImpl.class);
    private final List<String> extensionClassNames;

    public ExtensionDefinitionLoaderImpl(final List<String> extensionClassNames) {
        this.extensionClassNames = new ArrayList<>(extensionClassNames);
    }

    @Override
    public Collection<ExtensionDefinition> load() {
        LOG.debug("start to load extension definitions from {}", extensionClassNames);
        List<ExtensionDefinition> definitions = new ArrayList<>();
        for (String extensionClassName : extensionClassNames) {
            ExtensionDefinition definition = parse(extensionClassName);
            definitions.add(definition);
        }
        LOG.debug("extension definitions has been successfully loaded, {}", definitions);
        return definitions;
    }

    @SuppressWarnings("unchecked")
    private ExtensionDefinition parse(final String extensionClassName) {
        Class extensionClass = loadExtensionClass(extensionClassName);
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
        ConfigParameterDefinition config = null;
        if (!configs.isEmpty()) {
            AnnotatedParameter<ExtConfig> configParameter = configs.get(0);
            config = new ConfigParameterDefinition(configParameter.type,
                    configParameter.annotation.optional(), configParameter.position);
        }
        List<AnnotatedParameter<ExtDependency>> dependencyParams =
                findParameterWithAnnotation(constructor, ExtDependency.class);
        List<ExtensionDependencyDefinition> dependencies = new ArrayList<>();
        for (AnnotatedParameter<ExtDependency> param : dependencyParams) {
            if (Collection.class.isAssignableFrom(param.type)) {
                if (!param.type.equals(List.class)) {
                    LOG.error("for multiple dependency {} must be used, found: {} in {}",
                            List.class, param.type, extensionClassName);
                    throw new UnresolvableConstructorException(extensionClass, constructor, param.type);
                }
                dependencies.add(ExtensionDependencyDefinition.multiple(
                        param.annotation.qualifier(),
                        resolveCollectionGeneric(constructor, param),
                        param.annotation.optional(),
                        param.position)
                );
            } else {
                dependencies.add(ExtensionDependencyDefinition.single(
                        param.annotation.qualifier(), param.type, param.annotation.optional(), param.position));
            }
        }
        List<AbstractExtensionParameterDefinition> parameters = new ArrayList<>();
        if (config != null) {
            parameters.add(config);
        }
        parameters.addAll(dependencies);
        validateConstrictor(extensionClass, constructor, parameters);
        return ExtensionDefinition.builder()
                .withName(extensionName)
                .withExtension(extensionPointClass, extensionClass)
                .withConfig(config)
                .withDependencies(dependencies)
                .build();
    }

    private Class<?> resolveCollectionGeneric(final Constructor<?> constructor,
                                              final AnnotatedParameter<ExtDependency> param) {
        ParameterizedType type = (ParameterizedType) constructor.getGenericParameterTypes()[param.position];
        return (Class<?>) type.getActualTypeArguments()[0];
    }

    private void validateConstrictor(final Class extensionClass,
                                     final Constructor<?> constructor,
                                     final List<? extends AbstractExtensionParameterDefinition> parameters) {
        Set<Integer> unresolvedParametersNumbers = new HashSet<>();
        for (int number = 0; number < constructor.getParameterCount(); ++number) {
            unresolvedParametersNumbers.add(number);
        }
        for (AbstractExtensionParameterDefinition parameter : parameters) {
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

    private Class loadExtensionClass(final String name) {
        Class extClass = loadClass(name);
        Extension extension = (Extension) extClass.getAnnotation(Extension.class);
        if (extension == null) {
            LOG.error("extension class must have {} annotation on the class declaration", Extension.class.getName());
            throw new ConfigurationException("extension class must have " + Extension.class.getName() + " annotation");
        }
        return extClass;
    }

    private String loadExtensionName(final Class extClass) {
        Extension extension = (Extension) extClass.getAnnotation(Extension.class);
        return extension.name();
    }

    private Class loadExtensionPointClass(final Class extClass) {
        Extension extension = (Extension) extClass.getAnnotation(Extension.class);
        Class extPointClass = extension.point();
        ExtPoint extPoint = (ExtPoint) extPointClass.getAnnotation(ExtPoint.class);
        if (extPoint == null) {
            LOG.error("extension point class must have {} annotation on the class declaration", ExtPoint.class);
            throw new ExtensionDescriptorException("extension point class must have " + ExtPoint.class + " annotation");
        }
        return extPointClass;
    }

    private Class loadClass(final String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            LOG.error("class specified in extension meta info: \"{}\" is not found in classpath", name);
            throw new ExtensionClassNotFoundException(name);
        }
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
