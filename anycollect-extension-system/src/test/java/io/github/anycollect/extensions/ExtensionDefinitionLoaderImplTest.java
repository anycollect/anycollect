package io.github.anycollect.extensions;

import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtPoint;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.definitions.ExtensionDefinition;
import io.github.anycollect.extensions.definitions.ExtensionDependencyDefinition;
import io.github.anycollect.extensions.exceptions.ConfigurationException;
import io.github.anycollect.extensions.exceptions.ExtensionClassNotFoundException;
import io.github.anycollect.extensions.exceptions.ExtensionDescriptorException;
import io.github.anycollect.extensions.exceptions.WrongExtensionMappingException;
import io.github.anycollect.extensions.samples.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class ExtensionDefinitionLoaderImplTest {
    @Test
    @DisplayName("simple extension with config must be loaded correctly")
    void simpleExtensionMustBeLoadedCorrectly() {
        ExtensionDefinitionLoaderImpl loader = create(SampleExtension.class);
        Collection<ExtensionDefinition> definitions = loader.load();
        assertThat(definitions).containsExactly(ExtensionDefinition.builder()
                .withName("Sample")
                .withExtension(SampleExtensionPoint.class, SampleExtension.class)
                .withConfig(SampleExtensionConfig.class, false)
                .build());
    }

    @Test
    @DisplayName("extensions with dependencies must be loaded correctly")
    void extensionWithDependencyMustBeLoadedCorrectly() {
        ExtensionDefinitionLoaderImpl loader = create(SampleExtensionWithDependency.class);
        Collection<ExtensionDefinition> definitions = loader.load();
        ExtensionDependencyDefinition dependency = new ExtensionDependencyDefinition(
                "delegate",
                SampleExtensionPoint.class,
                false,
                0);
        assertThat(definitions).containsExactly(ExtensionDefinition.builder()
                .withName("SampleWithDependency")
                .withExtension(SampleExtensionPoint.class, SampleExtensionWithDependency.class)
                .withConfig(SampleExtensionConfig.class, false)
                .withDependencies(Collections.singletonList(dependency))
                .build());
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

    private static ExtensionDefinitionLoaderImpl create(Class<?> extensionClass) {
        return create(extensionClass.getName());
    }

    private static ExtensionDefinitionLoaderImpl create(String extensionClassName) {
        return new ExtensionDefinitionLoaderImpl(Collections.singletonList(extensionClassName));
    }
}