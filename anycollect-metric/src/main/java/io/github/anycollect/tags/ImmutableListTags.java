package io.github.anycollect.tags;

import io.github.anycollect.metric.Tag;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public final class ImmutableListTags implements Tags {
    private final List<Tag> tags;
    private final int hash;

    public static ImmutableListTags of(final Iterable<Tag> tags) {
        List<Tag> result = new ArrayList<>();
        for (Tag tag : tags) {
            result.add(tag);
        }
        return new ImmutableListTags(result);
    }

    private ImmutableListTags(final List<Tag> tags) {
        this.tags = tags;
        this.hash = Objects.hash(this.tags);
    }

    @Override
    public boolean hasTagKey(final String key) {
        return findTagIndex(key) != -1;
    }

    @Nonnull
    @Override
    public Tag getTag(final String key) {
        Tag tag = findTag(key);
        if (tag == null) {
            throw new IllegalArgumentException("there is no tag value associated with " + key + " key");
        }
        return tag;
    }

    @Nonnull
    @Override
    public Iterator<Tag> iterator() {
        return tags.iterator();
    }

    @Override
    public boolean isEmpty() {
        return tags.isEmpty();
    }

    @Nullable
    private Tag findTag(final String key) {
        int index = findTagIndex(key);
        if (index == -1) {
            return null;
        }
        return tags.get(index);
    }

    private int findTagIndex(final String key) {
        for (int i = 0; i < tags.size(); ++i) {
            if (tags.get(i).getKey().equals(key)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tags)) {
            return false;
        }
        Tags that = (Tags) o;
        return Tags.equals(this, that);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
