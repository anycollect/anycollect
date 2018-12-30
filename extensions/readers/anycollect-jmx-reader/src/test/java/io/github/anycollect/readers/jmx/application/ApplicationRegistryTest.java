package io.github.anycollect.readers.jmx.application;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationRegistryTest {
    @Test
    void findApplicationTest() {
        Application dummy = new Application("dummy", new SimpleQueryMatcher("group", "label"), false);
        ApplicationRegistry registry = ApplicationRegistry.singleton(dummy);
        assertThat(registry.hasApplication("dummy")).isTrue();
        assertThat(registry.getApplication("dummy")).isSameAs(dummy);
        assertThat(registry.hasApplication("wrong")).isFalse();
        Assertions.assertThrows(IllegalArgumentException.class, () -> registry.getApplication("wrong"));
    }

    @Test
    void creationRegistryFromApplicationWithTheSameNameIsIllegal() {
        Application dummy1 = new Application("dummy", new SimpleQueryMatcher("group", "label1"), false);
        Application dummy2 = new Application("dummy", new SimpleQueryMatcher("group", "label2"), true);
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> new ApplicationRegistry(Lists.newArrayList(dummy1, dummy2)));
        assertThat(ex).hasMessageContaining("dummy");
    }
}