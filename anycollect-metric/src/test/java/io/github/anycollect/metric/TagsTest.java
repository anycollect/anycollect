package io.github.anycollect.metric;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tags tests")
class TagsTest {
    @Test
    @DisplayName("tags must be iterable in creation order")
    void mustBeIterableInCreationOrder() {
        Tags tags = Tags.builder()
                .tag("key2", "value2")
                .tag("key1", "value1")
                .build();
        assertThat(tags.getTagKeys()).containsExactly("key2", "key1");
        assertThat(tags).containsExactly(Tag.of("key2", "value2"), Tag.of("key1", "value1"));
    }
}