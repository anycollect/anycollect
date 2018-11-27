package io.github.anycollect.extensions.jackson;

import io.github.anycollect.extensions.definitions.ExtensionDefinition;
import io.github.anycollect.extensions.exceptions.ConfigurationException;
import io.github.anycollect.extensions.exceptions.ExtensionClassNotFoundException;
import io.github.anycollect.extensions.exceptions.MissingRequiredPropertyException;
import io.github.anycollect.extensions.exceptions.WrongExtensionMappingException;
import io.github.anycollect.extensions.samples.SampleExtension;
import io.github.anycollect.extensions.samples.SampleExtensionConfig;
import io.github.anycollect.extensions.samples.SampleExtensionPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JacksonExtensionDefinitionLoaderTest {
    @Test
    @DisplayName("classes must be loaded correctly")
    void classesMustBeLoadedCorrectly() throws IOException {
        File meta = new File("src/test/resources/definitions/anycollect-meta.yaml");
        try (FileReader reader = new FileReader(meta)) {
            JacksonExtensionDefinitionLoader loader = new JacksonExtensionDefinitionLoader(reader);
            Collection<ExtensionDefinition> definitions = loader.load();
            assertThat(definitions).containsExactly(ExtensionDefinition.builder()
                    .withName("Sample")
                    .withExtension(SampleExtensionPoint.class, SampleExtension.class)
                    .withConfig(SampleExtensionConfig.class)
                    .build());
        }
    }

    @Test
    @DisplayName("name of extension must be specified in configuration")
    void nameOfExtensionIsRequired() throws IOException {
        File meta = new File("src/test/resources/definitions/anycollect-meta-missed-name.yaml");
        try (FileReader reader = new FileReader(meta)) {
            JacksonExtensionDefinitionLoader loader = new JacksonExtensionDefinitionLoader(reader);
            MissingRequiredPropertyException ex = Assertions.assertThrows(MissingRequiredPropertyException.class, loader::load);
            assertThat(ex.getProperty()).isEqualTo("extensionName");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"anycollect-meta-wrong-ext-class.yaml", "anycollect-meta-wrong-ext-point-class.yaml"})
    @DisplayName("extension classes must be present in classpath")
    void extensionPointClassMustBePresentInClassPath(String metaFileName) throws IOException {
        File meta = new File("src/test/resources/definitions/" + metaFileName);
        try (FileReader reader = new FileReader(meta)) {
            JacksonExtensionDefinitionLoader loader = new JacksonExtensionDefinitionLoader(reader);
            ExtensionClassNotFoundException ex = Assertions.assertThrows(ExtensionClassNotFoundException.class, loader::load);
            assertThat(ex.getClassName()).contains("Wrong");
        }
    }

    @Test
    @DisplayName("extension class must implement or extend extension point class")
    void extensionMustImplementOrExtendExtensionPointClass() throws IOException {
        File meta = new File("src/test/resources/definitions/anycollect-meta-fake-extension.yaml");
        try (FileReader reader = new FileReader(meta)) {
            JacksonExtensionDefinitionLoader loader = new JacksonExtensionDefinitionLoader(reader);
            WrongExtensionMappingException ex = Assertions.assertThrows(WrongExtensionMappingException.class, loader::load);
            assertThat(ex.getExtensionClass()).isEqualTo(FakeSampleExtension.class);
            assertThat(ex.getExtensionPointClass()).isEqualTo(SampleExtensionPoint.class);
        }
    }

    @Test
    @DisplayName("if source is unable to be read then high level exception must be thrown")
    void mustWrapLowLevelExceptionsIntoHighLevel() throws IOException{
        Reader thrower = mock(Reader.class);
        when(thrower.read(any(), anyInt(), anyInt())).thenThrow(IOException.class);
        JacksonExtensionDefinitionLoader loader = new JacksonExtensionDefinitionLoader(thrower);
        ConfigurationException ex = Assertions.assertThrows(ConfigurationException.class, loader::load);
        assertThat(ex).hasMessageContaining("unexpected error");
    }
}