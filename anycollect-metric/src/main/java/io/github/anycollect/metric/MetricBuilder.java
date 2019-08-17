package io.github.anycollect.metric;

import javax.annotation.Nonnull;

final class MetricBuilder implements
        Metric.KeyStageBuilder,
        Metric.TagsStageBuilder,
        Metric.MetaStageBuilder,
        Metric.Factory {
    private Key key;
    private Tags tags = Tags.empty();
    private Tags meta = Tags.empty();

    @Nonnull
    @Override
    public Metric.TagsStageBuilder key(@Nonnull final String key) {
        this.key = Key.of(key);
        return this;
    }

    @Nonnull
    @Override
    public Metric.TagsStageBuilder key(@Nonnull final Key key) {
        this.key = key;
        return this;
    }

    @Nonnull
    @Override
    public MetricBuilder empty() {
        return this;
    }

    @Nonnull
    @Override
    public Metric.MetaStageBuilder tag(@Nonnull final String key, @Nonnull final String value) {
        this.tags = Tags.of(key, value);
        return this;
    }

    @Nonnull
    @Override
    public Metric.MetaStageBuilder tags(@Nonnull final Tags tags) {
        this.tags = tags;
        return this;
    }

    @Nonnull
    @Override
    public Metric.Factory meta(@Nonnull final String key, @Nonnull final String value) {
        this.meta = Tags.of(key, value);
        return this;
    }

    @Nonnull
    @Override
    public Metric.Factory meta(@Nonnull final Tags meta) {
        this.meta = meta;
        return this;
    }

    @Nonnull
    @Override
    public Metric metric(@Nonnull final Stat stat, @Nonnull final Type type, @Nonnull final String unit) {
        return new ImmutableMetric(key, tags, meta, stat, type, unit);
    }
}
