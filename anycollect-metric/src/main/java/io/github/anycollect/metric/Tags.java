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

    boolean hasTagKey(String key);

    @Nonnull
    String getTagValue(String key);

    @Nonnull
    Set<String> getTagKeys();

    @Nonnull
    @Override
    Iterator<Tag> iterator();
}
