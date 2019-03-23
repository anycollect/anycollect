package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Set;

public interface Tags extends Iterable<Tag> {
    static ImmutableTags.Builder builder() {
        return new ImmutableTags.Builder();
    }

    static ImmutableTags empty() {
        return ImmutableTags.EMPTY;
    }

    static ImmutableTags of(@Nonnull String key, int value) {
        return builder().tag(key, Integer.toString(value)).build();
    }

    static ImmutableTags of(@Nonnull String key, @Nonnull String value) {
        return builder().tag(key, value).build();
    }

    static ImmutableTags concat(@Nonnull Tags left, @Nonnull Tags right) {
        return builder().concat(left).concat(right).build();
    }

    boolean hasTagKey(String key);

    @Nonnull
    String getTagValue(String key);

    @Nonnull
    Set<String> getTagKeys();

    @Nonnull
    @Override
    Iterator<Tag> iterator();

    boolean isEmpty();
}
