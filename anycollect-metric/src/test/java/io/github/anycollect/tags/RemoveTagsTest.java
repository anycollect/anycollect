package io.github.anycollect.tags;

import io.github.anycollect.metric.Tags;
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
            assertThat(RemoveTags.of(Tags.of("k", "v"), Collections.singleton("k"))).isSameAs(Tags.empty());
        }

        @Test
        void whenRemoveAllMultiTags() {
            assertThat(RemoveTags.of(
                    Tags.builder()
                            .tag("k1", "v1")
                            .tag("k2", "v2")
                            .build(),
                    Sets.newLinkedHashSet("k1", "k2"))
            ).isSameAs(Tags.empty());
        }
    }
}