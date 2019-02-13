package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Set;

@ToString(of = "tags", includeFieldNames = false)
@EqualsAndHashCode(of = "tags")
public final class ImmutableMetricId implements MetricId {
    @Getter
    private final Tags tags;
    @Getter
    private final Tags metaTags;

    public static Builder builder() {
        return new Builder();
    }

    private ImmutableMetricId(final Builder builder) {
        this.tags = builder.tagsBuilder.build();
        this.metaTags = builder.metaTagsBuilder.build();
        assertRequiredTag(tags, CommonTags.METRIC_KEY);
        assertRequiredTag(tags, CommonTags.UNIT);
        assertRequiredTag(tags, CommonTags.METRIC_TYPE);
        assertRequiredTag(tags, CommonTags.STAT);
    }

    private static void assertRequiredTag(final Tags tags, final CommonTags tag) {
        if (!tags.hasTagKey(tag.getKey())) {
            throw new IllegalArgumentException("tag \"" + tag.getKey() + "\" is required");
        }
    }

    @Override
    public String getKey() {
        return getTagValue(CommonTags.METRIC_KEY.getKey());
    }

    @Override
    public Stat getStat() {
        return Stat.parse(getTagValue(CommonTags.STAT.getKey()));
    }

    @Override
    public Type getType() {
        return Type.parse(getTagValue(CommonTags.METRIC_TYPE.getKey()));
    }

    @Override
    public String getUnit() {
        return getTagValue(CommonTags.UNIT.getKey());
    }

    @Override
    public boolean hasTagKey(@Nonnull final String key) {
        return tags.hasTagKey(key);
    }

    @Override
    public String getTagValue(@Nonnull final String key) {
        return tags.getTagValue(key);
    }

    @Override
    public Set<String> getTagKeys() {
        return tags.getTagKeys();
    }

    @Override
    public boolean hasMetaTagKey(@Nonnull final String key) {
        return metaTags.hasTagKey(key);
    }

    @Override
    public String getMetaTagValue(@Nonnull final String key) {
        return metaTags.getTagValue(key);
    }

    @Override
    public Set<String> getMetaTagKeys() {
        return metaTags.getTagKeys();
    }

    public static final class Builder {
        private final ImmutableTags.Builder tagsBuilder = Tags.builder();
        private final ImmutableTags.Builder metaTagsBuilder = Tags.builder();

        public Builder key(@Nonnull final String value) {
            tagsBuilder.tag(CommonTags.METRIC_KEY.getKey(), value);
            return this;
        }

        public Builder type(@Nonnull final Type value) {
            tagsBuilder.tag(CommonTags.METRIC_TYPE.getKey(), value.getTagValue());
            return this;
        }

        public Builder unit(@Nonnull final String value) {
            tagsBuilder.tag(CommonTags.UNIT.getKey(), value);
            return this;
        }

        public Builder stat(@Nonnull final Stat stat) {
            if (!Stat.isValid(stat)) {
                throw new IllegalArgumentException("stat " + stat + " is not valid");
            }
            tagsBuilder.tag(CommonTags.STAT.getKey(), stat.getTagValue());
            return this;
        }

        public Builder tag(@Nonnull final String key, @Nonnull final String value) {
            if (CommonTags.METRIC_KEY.getKey().equals(key)) {
                return key(value);
            }
            if (CommonTags.METRIC_TYPE.getKey().equals(key)) {
                return type(Type.parse(value));
            }
            if (CommonTags.UNIT.getKey().equals(key)) {
                return unit(value);
            }
            if (CommonTags.STAT.getKey().equals(key)) {
                return stat(Stat.parse(value));
            }
            tagsBuilder.tag(key, value);
            return this;
        }

        public Builder meta(@Nonnull final String key, @Nonnull final String value) {
            metaTagsBuilder.tag(key, value);
            return this;
        }

        public Builder concatTags(@Nonnull final Tags addition) {
            Objects.requireNonNull(addition, "addition must not be null");
            for (Tag tag : addition) {
                tag(tag.getKey(), tag.getValue());
            }
            return this;
        }

        public Builder concatMeta(@Nonnull final Tags addition) {
            Objects.requireNonNull(addition, "addition must not be null");
            for (Tag tag : addition) {
                meta(tag.getKey(), tag.getValue());
            }
            return this;
        }

        public ImmutableMetricId build() {
            return new ImmutableMetricId(this);
        }
    }
}
