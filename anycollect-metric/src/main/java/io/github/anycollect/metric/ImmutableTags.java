package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class ImmutableTags implements Tags {
    public static final ImmutableTags EMPTY = new ImmutableTags.Builder().build();
    private final List<Tag> tagList;
    private final int hash;

    public static ImmutableTags singleton(final String key, final String value) {
        return new ImmutableTags(key, value);
    }

    private ImmutableTags(final Builder builder) {
        Map<Key, String> tmpTagMap = new LinkedHashMap<>(builder.tags);
        List<Tag> tmpTagList = new ArrayList<>();
        for (Map.Entry<Key, String> entry : tmpTagMap.entrySet()) {
            tmpTagList.add(Tag.of(entry.getKey(), entry.getValue()));
        }
        this.tagList = Collections.unmodifiableList(tmpTagList);
        this.hash = Objects.hash(this.tagList);
    }

    private ImmutableTags(final String key, final String value) {
        this.tagList = Collections.singletonList(Tag.of(Key.of(key), value));
        this.hash = Objects.hash(this.tagList);
    }

    @Override
    public boolean hasTagKey(final CharSequence key) {
        Objects.requireNonNull(key, "tag key must not be null");
        return findTagOrNull(key) != null;
    }

    @Nonnull
    @Override
    public Tag getTag(final CharSequence key) {
        Objects.requireNonNull(key, "tag key must not be null");
        Tag tag = findTagOrNull(key);
        if (tag == null) {
            throw new IllegalArgumentException("there is no tag value associated with " + key + " key");
        }
        return tag;
    }

    @Nullable
    private Tag findTagOrNull(final CharSequence key) {
        for (final Tag tag : tagList) {
            if (tag.getKey().contentEquals(key)) {
                return tag;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public Iterator<Tag> iterator() {
        return tagList.iterator();
    }

    @Override
    public boolean isEmpty() {
        return tagList.isEmpty();
    }

    @Override
    public String toString() {
        return Tags.toString(this);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Tags)) {
            return false;
        }
        return Tags.equals(this, (Tags) obj);
    }

    public static final class Builder {
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
            return new ImmutableTags(this);
        }
    }
}
