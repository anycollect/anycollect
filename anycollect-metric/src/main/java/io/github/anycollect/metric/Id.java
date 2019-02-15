package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import java.util.Set;

public interface Id {
    Tags getTags();

    default boolean hasTagKey(@Nonnull String key) {
        return getTags().hasTagKey(key);
    }

    default String getTagValue(@Nonnull String key) {
        return getTags().getTagValue(key);
    }

    default Set<String> getTagKeys() {
        return getTags().getTagKeys();
    }

    Tags getMetaTags();

    default boolean hasMetaTagKey(@Nonnull String key) {
        return getMetaTags().hasTagKey(key);
    }

    default String getMetaTagValue(@Nonnull String key) {
        return getMetaTags().getTagValue(key);
    }

    default Set<String> getMetaTagKeys() {
        return getMetaTags().getTagKeys();
    }
}
