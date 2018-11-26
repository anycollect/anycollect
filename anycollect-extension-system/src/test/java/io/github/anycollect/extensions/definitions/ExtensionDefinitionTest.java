package io.github.anycollect.extensions.definitions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ExtensionDefinition tests")
class ExtensionDefinitionTest {
    @Test
    @DisplayName("fail fast if null was passed to builder")
    void nullsIsForbidden() {
        Assertions.assertThrows(NullPointerException.class, () -> ExtensionDefinition.builder().withName(null));
        Assertions.assertThrows(NullPointerException.class,
                () -> ExtensionDefinition.builder().withExtension(null, Extension.class));
        Assertions.assertThrows(NullPointerException.class,
                () -> ExtensionDefinition.builder().withExtension(ExtensionPoint.class, null));
    }

    @Test
    @DisplayName("extension class must implement extension point class")
    @SuppressWarnings("unchecked")
    void extensionClassMustImplementExtensionPointClass() {
        Assertions.assertDoesNotThrow(() -> ExtensionDefinition.builder().withExtension(ExtensionPoint.class, Extension.class));
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    Class spec = ExtensionPoint.class;
                    Class impl = FakeExtension.class;
                    ExtensionDefinition.builder().withExtension(spec, impl);
                });
        assertThat(ex).hasMessageContaining(FakeExtension.class.getName()).hasMessageContaining(Extension.class.getName());
    }

    @Test
    @DisplayName("config class is optional property")
    void configClassIsOptional() {
        Assertions.assertDoesNotThrow(
                () -> {
                    ExtensionDefinition definition = ExtensionDefinition.builder()
                            .withName("name")
                            .withExtension(ExtensionPoint.class, Extension.class)
                            .build();
                    assertThat(definition.getConfigClass()).isEmpty();
                });
    }

    @Test
    @DisplayName("name, extension point class, extension class is required")
    void allRequiredPropertiesMustBeSpecified() {
         IllegalStateException ex1 = Assertions.assertThrows(IllegalStateException.class,
                () -> ExtensionDefinition.builder().withName("nonnull").build());
        assertThat(ex1).hasMessageContaining("extension classes must be specified");
        IllegalStateException ex2 = Assertions.assertThrows(IllegalStateException.class,
                () -> ExtensionDefinition.builder().withExtension(ExtensionPoint.class, Extension.class).build());
        assertThat(ex2).hasMessageContaining("name must be specified");
    }

    interface ExtensionPoint {

    }

    private class Extension implements ExtensionPoint {

    }

    private class FakeExtension {

    }
}