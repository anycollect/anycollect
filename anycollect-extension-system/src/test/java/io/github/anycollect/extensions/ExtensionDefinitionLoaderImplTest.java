package io.github.anycollect.extensions;

import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtPoint;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.definitions.ConfigDefinition;
import io.github.anycollect.extensions.definitions.Definition;
import io.github.anycollect.extensions.definitions.MultiDependencyDefinition;
import io.github.anycollect.extensions.definitions.SingleDependencyDefinition;
import io.github.anycollect.extensions.exceptions.*;
import io.github.anycollect.extensions.samples.*;
import io.github.anycollect.extensions.utils.ConstrictorUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExtensionDefinitionLoaderImplTest {
    @Test
    @DisplayName("simple extension with config must be loaded correctly")
    void simpleExtensionMustBeLoadedCorrectly() {
        ExtensionDefinitionLoaderImpl loader = create(SampleExtension.class);
        Collection<Definition> definitions = loader.load();
        assertThat(definitions).containsExactly(Definition.builder()
                .withName("Sample")
                .withExtension(SampleExtensionPoint.class, ConstrictorUtils.createFor(SampleExtension.class, SampleExtensionConfig.class))
                .withConfig(new ConfigDefinition(SampleExtensionConfig.class, false, 0))
                .build());
    }

    @Test
    @DisplayName("extensions with dependencies must be loaded correctly")
    void extensionWithDependencyMustBeLoadedCorrectly() {
        ExtensionDefinitionLoaderImpl loader = create(SampleExtensionWithDependency.class);
        Collection<Definition> definitions = loader.load();
        assertThat(definitions).containsExactly(Definition.builder()
                .withName("SampleWithDependency")
                .withExtension(SampleExtensionPoint.class, ConstrictorUtils.createFor(
                        SampleExtensionWithDependency.class,
                        SampleExtensionPoint.class,
                        SampleExtensionConfig.class))
                .withSingleDependency(new SingleDependencyDefinition("delegate", SampleExtensionPoint.class, false, 0))
                .withConfig(new ConfigDefinition(SampleExtensionConfig.class, false, 1))
                .build());
    }

    @Test
    @DisplayName("multiple dependency must be supported")
    void multipleDependencyMustBeSupported() {
        Definition definition = loadSingle(MultipleDependency.OneMultipleDependency.class);
        assertThat(definition).isEqualTo(Definition.builder()
                .withName("OneMultipleDependency")
                .withExtension(SampleExtensionPoint.class, ConstrictorUtils.createFor(MultipleDependency.OneMultipleDependency.class, List.class))
                .withMultiDependency(new MultiDependencyDefinition(
                        "delegates",
                        SampleExtensionPoint.class,
                        false,
                        0
                ))
                .build()
        );
    }

    @Test
    @DisplayName("extension class must implement or extend extension point class")
    void extensionMustImplementOrExtendExtensionPointClass() {
        ExtensionDefinitionLoaderImpl loader = create(FakeImplementationSampleExtension.class);
        WrongExtensionMappingException ex = Assertions.assertThrows(WrongExtensionMappingException.class, loader::load);
        assertThat(ex.getExtensionClass()).isEqualTo(FakeImplementationSampleExtension.class);
        assertThat(ex.getExtensionPointClass()).isEqualTo(SampleExtensionPoint.class);
    }

    @Test
    @DisplayName("extension class must have correct annotation")
    void extensionMustHaveAnnotation() {
        ExtensionDefinitionLoaderImpl loader = create(FakeAnnotationSampleExtension.class);
        ConfigurationException ex = Assertions.assertThrows(ConfigurationException.class, loader::load);
        assertThat(ex).hasMessageContaining("annotation").hasMessageContaining(Extension.class.getName());
    }

    @Test
    @DisplayName("extension class must be present in classpath")
    void extensionClassMustBePresentInClassPath() {
        ExtensionDefinitionLoaderImpl loader = create("Fake");
        ExtensionClassNotFoundException ex = Assertions.assertThrows(ExtensionClassNotFoundException.class, loader::load);
        assertThat(ex.getClassName()).isEqualTo("Fake");
    }

    @Test
    @DisplayName("extension must have at most one config")
    void extensionMustHaveAtMostOneConfig() {
        ExtensionDefinitionLoaderImpl loader = create(ExtensionWithTwoConfigs.class);
        ExtensionDescriptorException ex = Assertions.assertThrows(ExtensionDescriptorException.class, loader::load);
        assertThat(ex).hasMessageContaining(ExtConfig.class.getName());
    }

    @Test
    @DisplayName("extension point must have annotation")
    void extensionPointMustHaveAnnotation() {
        ExtensionDefinitionLoaderImpl loader = create(ExtensionPointWithoutAnnotaionImpl.class);
        ExtensionDescriptorException ex = Assertions.assertThrows(ExtensionDescriptorException.class, loader::load);
        assertThat(ex).hasMessageContaining(ExtPoint.class.getName());
    }

    @Test
    @DisplayName("extension must have exactly one constructor")
    void extensionMustHaveExactlyOneConstructor() {
        ExtensionDefinitionLoaderImpl loader = create(ExtensionPointWithTwoConstructors.class);
        ExtensionDescriptorException ex = Assertions.assertThrows(ExtensionDescriptorException.class, loader::load);
        assertThat(ex).hasMessageContaining(ExtCreator.class.getName());
    }

    @Test
    @DisplayName("extension is able to not have a config")
    void extensionIsAbleToNotHaveConfig() {
        ExtensionDefinitionLoaderImpl loader = create(SampleExtensionWithoutConfig.class);
        assertThat(loader.load()).containsExactly(
                Definition.builder()
                        .withName("WithoutConfig")
                        .withExtension(SampleExtensionPoint.class, ConstrictorUtils.createFor(SampleExtensionWithoutConfig.class))
                        .build()
        );
    }

    @Test
    @DisplayName("fail if any constructor's parameters cannot be resolved")
    void failIfAnyConstructorParametersCannotBeResolved() {
        ExtensionDefinitionLoaderImpl loader = create(ExtensionWithUnresolvableParameter.class);
        ExtensionDescriptorException ex = Assertions.assertThrows(ExtensionDescriptorException.class, loader::load);
        assertThat(ex).hasMessageContaining(SampleExtensionConfig.class.getName());
    }

    @Test
    void concreteImplementationsOfCollectionForMultipleDependencyIsForbidden() {
        UnresolvableConstructorException ex = Assertions.assertThrows(UnresolvableConstructorException.class,
                () -> loadSingle(MultipleDependency.ArrayListDependency.class));
        assertThat(ex).hasMessageContaining(ArrayList.class.getName());
    }

    private static Definition loadSingle(Class<?> extensionClass) {
        Collection<Definition> load = create(extensionClass).load();
        assertThat(load).hasSize(1);
        return load.iterator().next();
    }

    private static ExtensionDefinitionLoaderImpl create(Class<?> extensionClass) {
        return create(extensionClass.getName());
    }

    private static ExtensionDefinitionLoaderImpl create(String extensionClassName) {
        return new ExtensionDefinitionLoaderImpl(Collections.singletonList(extensionClassName));
    }
}