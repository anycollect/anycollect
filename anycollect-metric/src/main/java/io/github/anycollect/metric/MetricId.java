package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

@ToString(of = "tags", includeFieldNames = false)
@EqualsAndHashCode(of = "tags")
public final class MetricId {
    public static final String METRIC_KEY_TAG = "what";
    public static final String METRIC_TYPE_TAG = "mtype";
    public static final String UNIT_TAG = "unit";
    public static final String STAT_TAG = "stat";

    @Getter
    private final Tags tags;
    @Getter
    private final Tags metaTags;

    public static Builder builder() {
        return new Builder();
    }

    private MetricId(final Builder builder) {
        this.tags = builder.tagsBuilder.build();
        this.metaTags = builder.metaTagsBuilder.build();
        assertRequiredTag(tags, METRIC_KEY_TAG);
        assertRequiredTag(tags, UNIT_TAG);
        assertRequiredTag(tags, METRIC_TYPE_TAG);
    }

    private static void assertRequiredTag(final Tags tags, final String key) {
        if (!tags.hasTagKey(key)) {
            throw new IllegalArgumentException("tag \"" + key + "\" is required");
        }
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

    public Type getType() {
        return Type.parse(getTagValue(METRIC_TYPE_TAG));
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

    public static final class Builder {
        private final Tags.Builder tagsBuilder = Tags.builder();
        private final Tags.Builder metaTagsBuilder = Tags.builder();

        public Builder key(final String value) {
            tagsBuilder.tag(METRIC_KEY_TAG, value);
            return this;
        }

        public Builder type(final Type value) {
            tagsBuilder.tag(METRIC_TYPE_TAG, value.getTagValue());
            return this;
        }

        public Builder unit(final String value) {
            tagsBuilder.tag(UNIT_TAG, value);
            return this;
        }

        public Builder stat(final Stat stat) {
            if (!Stat.isValid(stat)) {
                throw new IllegalArgumentException("stat " + stat + "is not valid");
            }
            tagsBuilder.tag(STAT_TAG, stat.getTagValue());
            return this;
        }

        public Builder tag(final String key, final String value) {
            if (METRIC_KEY_TAG.equals(key)) {
                return key(value);
            }
            if (METRIC_TYPE_TAG.equals(key)) {
                return type(Type.parse(value));
            }
            if (UNIT_TAG.equals(key)) {
                return unit(value);
            }
            if (STAT_TAG.equals(key)) {
                return stat(Stat.parse(value));
            }
            tagsBuilder.tag(key, value);
            return this;
        }

        public Builder meta(final String key, final String value) {
            metaTagsBuilder.tag(key, value);
            return this;
        }

        public Builder concatTags(final Tags addition) {
            Objects.requireNonNull(addition, "addition must not be null");
            for (Tag tag : addition) {
                tag(tag.getKey(), tag.getValue());
            }
            return this;
        }

        public Builder concatMeta(final Tags addition) {
            Objects.requireNonNull(addition, "addition must not be null");
            for (Tag tag : addition) {
                meta(tag.getKey(), tag.getValue());
            }
            return this;
        }

        public MetricId build() {
            return new MetricId(this);
        }
    }
}
