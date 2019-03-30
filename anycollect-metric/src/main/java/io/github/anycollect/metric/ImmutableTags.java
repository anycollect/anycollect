package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import java.util.*;

import static java.util.stream.Collectors.joining;

@EqualsAndHashCode(of = "tags")
public final class ImmutableTags implements Tags {
    public static final ImmutableTags EMPTY = new ImmutableTags.Builder().build();
    private final Map<String, String> tags;
    private final List<Tag> tagList;

    public static ImmutableTags singleton(final String key, final String value) {
        return new ImmutableTags(key, value);
    }

    private ImmutableTags(final Builder builder) {
        Map<String, String> tmpTagMap = new LinkedHashMap<>(builder.tags);
        List<Tag> tmpTagList = new ArrayList<>();
        for (Map.Entry<String, String> entry : tmpTagMap.entrySet()) {
            tmpTagList.add(Tag.of(entry.getKey(), entry.getValue()));
        }
        this.tags = Collections.unmodifiableMap(tmpTagMap);
        this.tagList = Collections.unmodifiableList(tmpTagList);
    }

    private ImmutableTags(final String key, final String value) {
        this.tagList = Collections.singletonList(Tag.of(key, value));
        this.tags = Collections.singletonMap(key, value);
    }

    @Override
    public boolean hasTagKey(final String key) {
        Objects.requireNonNull(key, "tag key must not be null");
        return tags.containsKey(key);
    }

    @Nonnull
    @Override
    public Tag getTag(final String key) {
        return Tag.of(key, getTagValue(key));
    }

    @Nonnull
    @Override
    public String getTagValue(final String key) {
        Objects.requireNonNull(key, "tag key must not be null");
        String value = tags.get(key);
        if (value == null) {
            throw new IllegalArgumentException("there is no tag value associated with " + key + " key");
        }
        return value;
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
        return tagList.stream().map(Tag::toString).collect(joining(","));
    }

    public static final class Builder {
        private final Map<String, String> tags = new LinkedHashMap<>();

        public Builder tag(@Nonnull final String key, final int value) {
            return tag(key, Integer.toString(value));
        }

        public Builder tag(@Nonnull final String key, @Nonnull final String value) {
            Objects.requireNonNull(key, " tag key must not be null");
            Objects.requireNonNull(value, " tag value must not be null");
            tags.put(key, value);
            return this;
        }

        public Builder concat(@Nonnull final Tags addition) {
            Objects.requireNonNull(addition, " tags must not be null");
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
