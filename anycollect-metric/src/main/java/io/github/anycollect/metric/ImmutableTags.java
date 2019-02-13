package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.*;

@ToString(of = "tags", includeFieldNames = false)
@EqualsAndHashCode(of = "tags")
public final class ImmutableTags implements Tags {
    public static final ImmutableTags EMPTY = new ImmutableTags.Builder().build();
    private final Map<String, String> tags;
    private final List<Tag> tagList;

    private ImmutableTags(final Builder builder) {
        Map<String, String> tmpTagMap = new LinkedHashMap<>(builder.tags);
        List<Tag> tmpTagList = new ArrayList<>();
        for (Map.Entry<String, String> entry : tmpTagMap.entrySet()) {
            tmpTagList.add(Tag.of(entry.getKey(), entry.getValue()));
        }
        this.tags = Collections.unmodifiableMap(tmpTagMap);
        this.tagList = Collections.unmodifiableList(tmpTagList);
    }

    @Override
    public boolean hasTagKey(final String key) {
        Objects.requireNonNull(key, "tag key must not be null");
        return tags.containsKey(key);
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
    public Set<String> getTagKeys() {
        return Collections.unmodifiableSet(tags.keySet());
    }

    @Nonnull
    @Override
    public Iterator<Tag> iterator() {
        return tagList.iterator();
    }

    public static final class Builder {
        private final Map<String, String> tags = new LinkedHashMap<>();

        public Builder tag(@Nonnull final String key, @Nonnull final String value) {
            Objects.requireNonNull(key, " tag key must not be null");
            Objects.requireNonNull(value, " tag value must not be null");
            tags.put(key, value);
            return this;
        }

        public ImmutableTags build() {
            return new ImmutableTags(this);
        }
    }
}
