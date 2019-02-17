package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;

@Getter
@ToString(of = "tags", includeFieldNames = false)
@EqualsAndHashCode(of = {"key", "unit", "tags"})
public final class ImmutableMeterId implements MeterId {
    private final String key;
    private final String unit;
    private final ImmutableTags tags;
    private final ImmutableTags metaTags;

    public ImmutableMeterId(@Nonnull final String key, @Nonnull final String unit,
                            @Nonnull final ImmutableTags tags, @Nonnull final ImmutableTags meta) {
        this.key = key;
        this.unit = unit;
        this.tags = tags;
        this.metaTags = meta;
    }

    private ImmutableMeterId(final Builder builder) {
        this.key = builder.getKey();
        this.unit = builder.getUnit();
        this.tags = builder.getTagsBuilder().build();
        this.metaTags = builder.getMetaBuilder().build();
    }

    public static final class Builder extends BaseBuilder<Builder> {
        @Override
        protected Builder self() {
            return this;
        }

        public Builder key(@Nonnull final String key) {
            return super.key(key);
        }

        public Builder unit(@Nonnull final String unit) {
            return super.unit(unit);
        }

        public ImmutableMeterId build() {
            return new ImmutableMeterId(this);
        }
    }
}
