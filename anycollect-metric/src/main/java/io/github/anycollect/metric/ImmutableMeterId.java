package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;

@ToString(of = "tags", includeFieldNames = false)
@EqualsAndHashCode(of = "tags")
public final class ImmutableMeterId implements MeterId {
    @Getter
    private final ImmutableTags tags;
    @Getter
    private final ImmutableTags metaTags;

    public ImmutableMeterId(@Nonnull final ImmutableTags tags, @Nonnull final ImmutableTags meta) {
        this.tags = tags;
        this.metaTags = meta;
    }

    private ImmutableMeterId(final Builder builder) {
        this.tags = builder.getTagsBuilder().build();
        this.metaTags = builder.getMetaBuilder().build();
        // TODO check if key and unit present
    }

    public static final class Builder extends BaseBuilder<Builder> {
        @Override
        protected Builder self() {
            return this;
        }

        public Builder key(@Nonnull final String value) {
            return super.key(value);
        }

        public Builder unit(@Nonnull final String value) {
            return super.unit(value);
        }

        public ImmutableMeterId build() {
            return new ImmutableMeterId(this);
        }
    }
}
