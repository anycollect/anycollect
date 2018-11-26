package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;

@EqualsAndHashCode(of = "tags")
public final class MetricId {
    @Getter
    private final Tags tags;
    @Getter
    private final Tags metaTags;

    public static Builder builder() {
        return new Builder();
    }

    private MetricId(final Builder builder) {
        this.tags = new Tags(builder.tags);
        this.metaTags = new Tags(builder.metaTags);
    }

    public boolean hasTagKey(final String key) {
        return tags.hasTagKey(key);
    }

    public String getTagValue(final String key) {
        return tags.getTagValue(key);
    }

    public Set<String> getTagKeys() {
        return tags.getTagKeys();
    }

    public boolean hasMetaTagKey(final String key) {
        return metaTags.hasTagKey(key);
    }

    public String getMetaTagValue(final String key) {
        return metaTags.getTagValue(key);
    }

    public Set<String> getMetaTagKeys() {
        return metaTags.getTagKeys();
    }

    public static final class Builder {
        private final List<Tag> tags = new ArrayList<>();
        private final List<Tag> metaTags = new ArrayList<>();

        public Builder tag(final String key, final String value) {
            return putKeyValueToTagList(key, value, tags);
        }

        public Builder meta(final String key, final String value) {
            return putKeyValueToTagList(key, value, metaTags);
        }

        private Builder putKeyValueToTagList(final String key, final String value, final List<Tag> list) {
            Objects.requireNonNull(key, "tag key must not be null");
            Objects.requireNonNull(value, "tag value must not be null");
            list.add(Tag.of(key, value));
            return this;
        }

        public MetricId build() {
            return new MetricId(this);
        }
    }
}
