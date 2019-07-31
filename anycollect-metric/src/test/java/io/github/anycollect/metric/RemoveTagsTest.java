package io.github.anycollect.metric;

import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class RemoveTagsTest {
    @Nested
    class ReturnsEmptyTags {
        @Test
        void whenGivenEmptyBase() {
            assertThat(RemoveTags.of(Tags.empty(), Collections.emptySet())).isSameAs(Tags.empty());
        }

        @Test
        void whenRemoveSingleTag() {
            assertThat(RemoveTags.of(Tags.of("k", "v"), Collections.singleton(Key.of("k")))).isSameAs(Tags.empty());
        }

        @Test
        void whenRemoveAllMultiTags() {
            assertThat(RemoveTags.of(
                    Tags.builder()
                            .tag("k1", "v1")
                            .tag("k2", "v2")
                            .build(),
                    Sets.newLinkedHashSet(Key.of("k1"), Key.of("k2")))
            ).isSameAs(Tags.empty());
        }
    }
}