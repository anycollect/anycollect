package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
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
        this.tags = builder.getTagsBuilder().build();
        this.metaTags = builder.getMetaBuilder().build();
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
    public Set<String> getMetaTagKeys() {
        return metaTags.getTagKeys();
    }

    public static final class Builder extends BaseBuilder<Builder> {
        @Override
        protected Builder self() {
            return this;
        }

        public Builder key(@Nonnull final String value) {
            return super.key(value);
        }

        public Builder type(@Nonnull final Type value) {
            return super.type(value);
        }

        public Builder unit(@Nonnull final String value) {
            return super.unit(value);
        }

        public Builder stat(@Nonnull final Stat stat) {
            return super.stat(stat);
        }

        public ImmutableMetricId build() {
            return new ImmutableMetricId(this);
        }
    }
}
