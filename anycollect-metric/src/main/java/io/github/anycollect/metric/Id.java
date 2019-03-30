package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public interface Id {
    Tags getTags();

    default boolean hasTagKey(@Nonnull String key) {
        return getTags().hasTagKey(key);
    }

    default String getTagValue(@Nonnull String key) {
        return getTags().getTagValue(key);
    }

    Tags getMetaTags();

    default boolean hasMetaTagKey(@Nonnull String key) {
        return getMetaTags().hasTagKey(key);
    }

    default String getMetaTagValue(@Nonnull String key) {
        return getMetaTags().getTagValue(key);
    }
}
