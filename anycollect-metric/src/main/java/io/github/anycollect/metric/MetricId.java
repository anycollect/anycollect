package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;

@EqualsAndHashCode(of = "tags")
public final class MetricId {
    public static final String METRIC_KEY_TAG = "what";
    public static final String METRIC_TYPE_TAG = "mtype";
    public static final String UNIT_TAG = "unit";
    public static final String STAT_TAG = "stat";
    private static final Set<String> SPECIAL_TAGS;

    static {
        Set<String> tmp = new HashSet<>();
        tmp.add(METRIC_KEY_TAG);
        tmp.add(METRIC_TYPE_TAG);
        tmp.add(UNIT_TAG);
        tmp.add(STAT_TAG);
        SPECIAL_TAGS = tmp;
    }

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

    public boolean hasKey() {
        return hasTagKey(METRIC_KEY_TAG);
    }

    public String getKey() {
        return getTagValue(METRIC_KEY_TAG);
    }

    public boolean hasStat() {
        return hasTagKey(STAT_TAG);
    }

    public Stat getStat() {
        return Stat.parse(getTagValue(STAT_TAG));
    }

    public boolean hasType() {
        return hasTagKey(METRIC_TYPE_TAG);
    }

    public Type getType() {
        return Type.parse(getTagValue(METRIC_TYPE_TAG));
    }

    public boolean hasUnit() {
        return hasTagKey(UNIT_TAG);
    }

    public String getUnit() {
        return getTagValue(UNIT_TAG);
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

    public static boolean isSpecialTag(final String tagKey) {
        return SPECIAL_TAGS.contains(tagKey);
    }

    public static final class Builder {
        private final List<Tag> tags = new ArrayList<>();
        private final List<Tag> metaTags = new ArrayList<>();

        public Builder key(final String value) {
            return putTag(METRIC_KEY_TAG, value);
        }

        public Builder type(final Type value) {
            return putTag(METRIC_TYPE_TAG, value.getTagValue());
        }

        public Builder unit(final String value) {
            return putTag(UNIT_TAG, value);
        }

        public Builder stat(final Stat stat) {
            if (!Stat.isValid(stat)) {
                throw new IllegalArgumentException("stat " + stat + "is not valid");
            }
            return putTag(STAT_TAG, stat.getTagValue());
        }

        public Builder tag(final String key, final String value) {
            assertIsNotSpecialTag(key);
            return putTag(key, value);
        }

        private Builder putTag(final String key, final String value) {
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

        private void assertIsNotSpecialTag(final String tagKey) {
            if (isSpecialTag(tagKey)) {
                throw new IllegalArgumentException("tag " + tagKey
                        + " has special meaning and must be set using special method");
            }
        }

        public MetricId build() {
            return new MetricId(this);
        }
    }
}
