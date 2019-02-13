package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;

@ToString(of = "tags", includeFieldNames = false)
@EqualsAndHashCode(of = "tags")
public final class ImmutableMeterId implements MeterId {
    @Getter
    private final ImmutableTags tags;
    @Getter
    private final ImmutableTags metaTags;

    private ImmutableMeterId(final Builder builder) {
        this.tags = builder.tagsBuilder.build();
        this.metaTags = builder.metaTagsBuilder.build();
        // TODO check if key and unit present
    }

    @Nonnull
    @Override
    public String getKey() {
        return tags.getTagValue(CommonTags.METRIC_KEY.getKey());
    }

    @Nonnull
    @Override
    public String getUnit() {
        return tags.getTagValue(CommonTags.UNIT.getKey());
    }

    @Override
    public MetricId counter(@Nullable final String unit) {
        return MetricId.builder()
                .concatTags(tags)
                .concatMeta(metaTags)
                .stat(Stat.value())
                .type(Type.COUNTER)
                .unit(unit == null ? getUnit() : unit)
                .build();
    }

    @Override
    public MetricId max(@Nullable final String unit) {
        return MetricId.builder()
                .concatTags(tags)
                .concatMeta(metaTags)
                .type(Type.GAUGE)
                .unit(unit == null ? getUnit() : unit)
                .stat(Stat.max())
                .build();
    }

    @Override
    public MetricId mean(@Nullable final String unit) {
        return MetricId.builder()
                .concatTags(tags)
                .concatMeta(metaTags)
                .type(Type.GAUGE)
                .unit(unit == null ? getUnit() : unit)
                .stat(Stat.mean())
                .build();
    }

    @Override
    public MetricId value(@Nullable final String unit) {
        return MetricId.builder()
                .concatTags(tags)
                .concatMeta(metaTags)
                .type(Type.GAUGE)
                .unit(unit == null ? getUnit() : unit)
                .stat(Stat.value())
                .build();
    }

    @Override
    public MetricId percentile(final int num, @Nullable final String unit) {
        return MetricId.builder()
                .concatTags(tags)
                .concatMeta(metaTags)
                .type(Type.GAUGE)
                .unit(unit == null ? getUnit() : unit)
                .stat(Stat.percentile(num))
                .build();
    }

    @Override
    public MetricId percentile(final double percentile, @Nullable final String unit) {
        return MetricId.builder()
                .concatTags(tags)
                .concatMeta(metaTags)
                .type(Type.GAUGE)
                .unit(unit == null ? getUnit() : unit)
                .stat(Stat.percentile(percentile))
                .build();
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

        public Builder unit(@Nonnull final String value) {
            tagsBuilder.tag(CommonTags.UNIT.getKey(), value);
            return this;
        }

        public Builder tag(@Nonnull final String key, @Nonnull final String value) {
            if (CommonTags.METRIC_KEY.getKey().equals(key)) {
                return key(value);
            }
            if (CommonTags.UNIT.getKey().equals(key)) {
                return unit(value);
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

        public ImmutableMeterId build() {
            return new ImmutableMeterId(this);
        }
    }
}
