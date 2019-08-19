package io.github.anycollect.metric;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.*;

@Immutable
@API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
public interface Tags extends Iterable<Tag> {
    static Builder builder() {
        return new Builder();
    }

    static ImmutableTags empty() {
        return ImmutableTags.EMPTY;
    }

    static Tags of(@Nonnull String key, int value) {
        return ImmutableTags.singleton(key, Integer.toString(value));
    }

    static Tags of(@Nonnull Key key, int value) {
        return ImmutableTags.singleton(key, Integer.toString(value));
    }

    static Tags of(@Nonnull String key, @Nonnull String value) {
        return ImmutableTags.singleton(key, value);
    }

    static Tags of(@Nonnull Key key, @Nonnull String value) {
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

    boolean hasTagKey(Key key);

    default boolean hasTagKey(String key) {
        // TODO tune
        return hasTagKey(Key.of(key));
    }

    @Nonnull
    Tag getTag(Key key);

    @Nonnull
    default Tag getTag(String key) {
        // TODO tune
        return getTag(Key.of(key));
    }

    @Nonnull
    default String getTagValue(Key key) {
        return getTag(key).getValue();
    }

    @Nonnull
    default String getTagValue(String key) {
        // TODO tune
        return getTagValue(Key.of(key));
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

    @Nonnull
    static String toString(@Nullable final Tags tags) {
        if (tags == null) {
            return "null";
        }
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

    final class Builder {
        private final Map<Key, String> tags = new LinkedHashMap<>();

        public Builder tag(@Nonnull final String key, final int value) {
            return tag(key, Integer.toString(value));
        }

        public Builder tag(@Nonnull final String key, @Nonnull final String value) {
            Objects.requireNonNull(key, "tag key must not be null");
            Objects.requireNonNull(value, "tag value must not be null");
            tags.put(Key.of(key), value);
            return this;
        }

        public Builder concat(@Nonnull final Tags addition) {
            Objects.requireNonNull(addition, "tags must not be null");
            for (Tag tag : addition) {
                tags.put(tag.getKey(), tag.getValue());
            }
            return this;
        }

        public ImmutableTags build() {
            return new ImmutableTags(this.tags);
        }
    }
}
