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

    static Tags of(@Nonnull String key, int value) {
        return builder().tag(key, Integer.toString(value)).build();
    }

    static Tags of(@Nonnull String key, @Nonnull String value) {
        return ImmutableTags.singleton(key, value);
    }

    static Tags of(@Nonnull String... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("array must contain even number of elements");
        }
        ImmutableTags.Builder builder = new ImmutableTags.Builder();
        for (int i = 0; i < keyValues.length / 2; i++) {
            builder.tag(keyValues[2 * i], keyValues[2 * i + 1]);
        }
        return builder.build();
    }

    boolean hasTagKey(CharSequence key);

    @Nonnull
    Tag getTag(CharSequence key);

    @Nonnull
    default String getTagValue(CharSequence key) {
        return getTag(key).getValue();
    }

    @Nonnull
    @Override
    Iterator<Tag> iterator();

    boolean isEmpty();

    default Tags concat(Tags tags) {
        return ConcatTags.of(this, tags);
    }

    default Tags remove(String key) {
        return remove(Key.of(key));
    }

    default Tags remove(Key key) {
        return RemoveTags.of(this, key);
    }

    default Tags remove(Set<Key> keys) {
        return RemoveTags.of(this, keys);
    }

    default boolean contains(Tags tags) {
        for (Tag tag : tags) {
            if (!hasTagKey(tag.getKey())) {
                return false;
            }
            if (!getTag(tag.getKey()).equals(tag)) {
                return false;
            }
        }
        return true;
    }

    static boolean equals(final Tags left, final Tags right) {
        if (left == right) {
            return true;
        }
        return left.contains(right) && right.contains(left);
    }

    static String toString(@Nonnull final Tags tags) {
        if (tags.isEmpty()) {
            return "{}";
        }
        StringBuilder output = new StringBuilder();
        boolean first = true;
        for (final Tag tag : tags) {
            if (!first) {
                output.append(',');
            }
            output.append(tag.getKey()).append("=").append(tag.getValue());
            first = false;
        }
        return output.toString();
    }
}
