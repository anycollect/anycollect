package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.Set;

@Getter
@ToString(of = "tags", includeFieldNames = false)
@EqualsAndHashCode(of = {"key", "unit", "stat", "type", "tags"})
public final class ImmutableMetricId implements MetricId {
    private final String key;
    private final String unit;
    private final Stat stat;
    private final Type type;
    private final Tags tags;
    private final Tags metaTags;

    public static Builder key(@Nonnull final String key) {
        return new Builder(key);
    }

    private ImmutableMetricId(final Builder builder) {
        this.key = builder.getKey();
        this.unit = builder.getUnit();
        this.stat = builder.getStat();
        this.type = builder.getType();
        this.tags = builder.getTagsBuilder().build();
        this.metaTags = builder.getMetaBuilder().build();
    }

    @Override
    public Set<String> getMetaTagKeys() {
        return metaTags.getTagKeys();
    }

    public static final class Builder extends BaseBuilder<Builder> {
        public Builder(@Nonnull final String key) {
            key(key);
        }

        @Override
        protected Builder self() {
            return this;
        }

        public Builder type(@Nonnull final Type type) {
            return super.type(type);
        }

        public Builder unit(@Nonnull final String unit) {
            return super.unit(unit);
        }

        public Builder stat(@Nonnull final Stat stat) {
            return super.stat(stat);
        }

        public ImmutableMetricId build() {
            return new ImmutableMetricId(this);
        }
    }
}
