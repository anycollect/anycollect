package io.github.anycollect.extensions.definitions;

import io.github.anycollect.extensions.samples.SampleExtension;
import io.github.anycollect.extensions.samples.SampleExtensionPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class ExtensionDependencyDefinitionTest {
    private ExtensionDefinition definition = ExtensionDefinition.builder()
            .withName("Sample")
            .withExtension(SampleExtensionPoint.class, SampleExtension.class)
            .build();

    private ExtensionInstanceDefinition instance = new ExtensionInstanceDefinition(definition,
            "sample", null, Collections.emptyMap());

    @Test
    @DisplayName("test single dependency")
    void singleDependency() {
        ExtensionInstanceDependencyDefinition dependency = new ExtensionInstanceDependencyDefinition(instance);
        assertThat(dependency.isSingle()).isTrue();
        assertThat(dependency.getInstance()).isEqualTo(instance);
    }

    @Test
    void listWithOneElementWillLeadToMultiDependency() {
        ExtensionInstanceDependencyDefinition dependency =
                new ExtensionInstanceDependencyDefinition(Collections.singletonList(instance));
        assertThat(dependency.isSingle()).isFalse();
        Assertions.assertThrows(IllegalStateException.class, dependency::getInstance);
    }

    @Test
    @DisplayName("test multiple dependency")
    void testMultiple() {
        ExtensionInstanceDependencyDefinition dependency = new ExtensionInstanceDependencyDefinition(Arrays.asList(instance, instance));
        assertThat(dependency.isSingle()).isFalse();
        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class, dependency::getInstance);
        assertThat(ex).hasMessageContaining("list");
    }
}