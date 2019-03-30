package io.github.anycollect.metric;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tags tests")
class ImmutableTagsTest {
    @Test
    @DisplayName("tags must be iterable in creation order")
    void mustBeIterableInCreationOrder() {
        Tags tags = Tags.builder()
                .tag("key2", "value2")
                .tag("key1", "value1")
                .build();
        assertThat(tags).containsExactly(Tag.of("key2", "value2"), Tag.of("key1", "value1"));
    }

    @Test
    void singletonTagTest() {
        Tags tags = Tags.of("test", "value");
        assertThat(tags).containsExactly(Tag.of("test", "value"));
    }

    @Test
    void emptyTagsMustBeSingleton() {
        assertThat(Tags.empty()).isSameAs(Tags.empty());
    }
}