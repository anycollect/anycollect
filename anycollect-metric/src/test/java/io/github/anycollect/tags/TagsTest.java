package io.github.anycollect.tags;

import io.github.anycollect.metric.Tag;
import io.github.anycollect.metric.Tags;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TagsTest {
    @Nested
    @DisplayName("when create empty tags")
    class WhenCreateEmptyTags {
        private Tags empty;

        @BeforeEach
        void setUp() {
            empty = Tags.empty();
        }

        @Test
        @DisplayName("returned value is not null")
        void tagsIsNotNull() {
            assertThat(empty).isNotNull();
        }

        @Test
        @DisplayName("tags is empty")
        void isEmpty() {
            assertThat(empty.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("iterator is empty")
        void iteratorIsEmpty() {
            assertThat(empty).isEmpty();
        }

        @Nested
        @DisplayName("when add tag")
        class WhenAddTag {
            private Tags withTag;
            private Tags addition;

            @BeforeEach
            void setUp() {
                addition = Tags.of("a1", "a1");
                withTag = empty.concat(addition);
            }

            @Test
            @DisplayName("result is equal to addition")
            void resultIsEqualToAddition() {
                assertThat(withTag).isSameAs(addition);
            }

            @Nested
            @DisplayName("when remove added tag")
            class WhenRemoveAddedTag {
                private Tags afterRemove;

                @BeforeEach
                void setUp() {
                    afterRemove = withTag.remove("a1");
                }

                @Test
                @DisplayName("result is empty")
                void resultIsEmpty() {
                    assertThat(afterRemove).isSameAs(Tags.empty());
                }
            }

            @Nested
            @DisplayName("when remove other tag")
            class WhenRemoveOtherTag {
                private Tags afterRemove;

                @BeforeEach
                void setUp() {
                    afterRemove = withTag.remove("other");
                }

                @Test
                @DisplayName("result is same as before")
                void resultIsSameAsBefore() {
                    assertThat(afterRemove).isSameAs(withTag);
                }
            }

            @Nested
            @DisplayName("when add some more tags")
            class WhenAddSomeMoreTags {
                private Tags withSomeMoreTags;
                private Tags someMoreTags;

                @BeforeEach
                void setUp() {
                    someMoreTags = Tags.builder()
                            .tag("k2", "v2")
                            .tag("k3", "v3")
                            .build();
                    withSomeMoreTags = withTag.concat(someMoreTags);
                }

                @Test
                @DisplayName("result contains all of them")
                void resultContainsAllOfThem() {
                    assertThat(withSomeMoreTags).containsExactly(
                            Tag.of("a1", "a1"),
                            Tag.of("k2", "v2"),
                            Tag.of("k3", "v3")
                    );
                }

                @Nested
                @DisplayName("when add tag that is already present in tags")
                class WhenAddExistingTag {
                    private Tags afterOverrideTag;

                    @BeforeEach
                    void setUp() {
                        afterOverrideTag = withSomeMoreTags.concat(Tags.of("a1", "a2"));
                    }

                    @Test
                    @DisplayName("iteration order is not changed")
                    void iterationOrderIsNotChanged() {
                        assertThat(afterOverrideTag).containsExactly(
                                Tag.of("a1", "a2"),
                                Tag.of("k2", "v2"),
                                Tag.of("k3", "v3")
                        );
                    }

                    @Test
                    @DisplayName("tag key is still present")
                    void tagKeyIsStillPresent() {
                        assertThat(afterOverrideTag.hasTagKey("a1")).isTrue();
                    }

                    @Test
                    @DisplayName("value is overridden")
                    void valueIsOverridden() {
                        assertThat(afterOverrideTag.getTagValue("a1")).isEqualTo("a2");
                    }

                    @Test
                    @DisplayName("tag is overridden")
                    void tagIsOverridden() {
                        assertThat(afterOverrideTag.getTag("a1")).isEqualTo(Tag.of("a1", "a2"));
                    }
                }

                @Nested
                @DisplayName("when remove some tags")
                class WhenRemoveSomeTags {
                    private Tags afterRemove;

                    @BeforeEach
                    void setUp() {
                        afterRemove = withSomeMoreTags.remove(Sets.newLinkedHashSet("a1", "k3"));
                    }

                    @Test
                    @DisplayName("iterator does not iterate through them")
                    void iteratorDoesNotIterateThroughThem() {
                        assertThat(afterRemove).containsExactly(
                                Tag.of("k2", "v2")
                        );
                    }

                    @Test
                    @DisplayName("tag keys are absent")
                    void tagKeysAreAbsent() {
                        assertThat(afterRemove.hasTagKey("a1")).isFalse();
                        assertThat(afterRemove.hasTagKey("k3")).isFalse();
                    }

                    @Test
                    @DisplayName("try to get these tags will lead to exception")
                    void tryToGetTheseTagsIsLeadToException() {
                        assertThatThrownBy(() -> afterRemove.getTag("a1"))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessageContaining("a1");
                        assertThatThrownBy(() -> afterRemove.getTag("k3"))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessageContaining("k3");
                    }
                }
            }
        }
    }
}
