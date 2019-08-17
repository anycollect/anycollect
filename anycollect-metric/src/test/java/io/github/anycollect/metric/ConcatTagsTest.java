package io.github.anycollect.metric;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConcatTagsTest {
    @Test
    void baseTagMustBeOverriddenByDelta() {
        ImmutableTags tags1 = Tags.builder()
                .tag("k1", "v1")
                .tag("k2", "v2")
                .build();
        ImmutableTags tags2 = Tags.builder()
                .tag("k2", "v2o")
                .tag("k3", "v3")
                .build();
        Tags tags = ConcatTags.of(tags1, tags2);
        assertThat(tags).containsExactly(
                Tag.of("k1", "v1"),
                Tag.of("k2", "v2o"),
                Tag.of("k3", "v3")
        );
    }

    @Test
    void overrideAll() {
        ImmutableTags tags1 = Tags.builder()
                .tag("k1", "v1")
                .tag("k2", "v2")
                .build();
        ImmutableTags tags2 = Tags.builder()
                .tag("k2", "v2o")
                .tag("k1", "v1o")
                .build();
        Tags tags = ConcatTags.of(tags1, tags2);
        assertThat(tags).containsExactly(
                Tag.of("k1", "v1o"),
                Tag.of("k2", "v2o")
        );
    }

    @Nested
    @DisplayName("when base is empty")
    class WhenBaseIsEmpty {
        private Tags base = Tags.empty();

        @Test
        @DisplayName("concatenation is equal to delta")
        void concatenationIsEqualToDelta() {
            Tags delta = Tags.builder().tag("t", "t").build();
            Tags concat = base.concat(delta);
            assertThat(concat).isSameAs(delta);
        }
    }

    @Nested
    @DisplayName("when delta is empty")
    class WhenDeltaIsEmpty {
        private Tags delta = Tags.empty();

        @Test
        @DisplayName("concatenation is equal to base")
        void concatenationIsEqualToBase() {
            Tags base = Tags.builder().tag("t", "t").build();
            Tags concat = base.concat(delta);
            assertThat(concat).isSameAs(base);
        }
    }
}