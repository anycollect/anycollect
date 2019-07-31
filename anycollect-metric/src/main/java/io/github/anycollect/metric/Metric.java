package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public interface Metric {
    static KeyStageBuilder builder() {
        return new Builder();
    }

    @Nonnull
    Key getKey();

    @Nonnull
    Tags getTags();

    @Nonnull
    Tags getMeta();

    @Nonnull
    Stat getStat();

    @Nonnull
    Type getType();

    @Nonnull
    String getUnit();

    @Nonnull
    default MutableMetric modify() {
        return new MutableMetricImpl(this);
    }

    default Sample sample(double value, long timestamp) {
        return new DoubleSample(this, value, timestamp);
    }

    interface KeyStageBuilder extends MetricBuilder {
        TagsStageBuilder key(@Nonnull String key);

        TagsStageBuilder key(@Nonnull Key key);
    }

    interface TagsStageBuilder extends MetricBuilder {
        default MetaStageBuilder empty() {
            return tags(Tags.empty());
        }

        MetaStageBuilder tag(@Nonnull String key, @Nonnull String value);

        MetaStageBuilder tags(@Nonnull Tags tags);
    }

    interface MetaStageBuilder extends MetricBuilder {
        default MetricBuilder empty() {
            return meta(Tags.empty());
        }

        MetricBuilder meta(@Nonnull String key, @Nonnull String value);

        MetricBuilder meta(@Nonnull Tags meta);
    }

    interface MetricBuilder {
        default Metric counter() {
            return counter("");
        }

        default Metric counter(@Nonnull final String unit) {
            return metric(Stat.VALUE, Type.COUNTER, unit);
        }

        default Metric min() {
            return min("");
        }

        default Metric min(@Nonnull final String unit) {
            return metric(Stat.MIN, Type.AGGREGATE, unit);
        }

        default Metric max() {
            return max("");
        }

        default Metric max(@Nonnull final String unit) {
            return metric(Stat.MAX, Type.AGGREGATE, unit);
        }

        default Metric mean() {
            return mean("");
        }

        default Metric mean(@Nonnull final String unit) {
            return metric(Stat.MEAN, Type.AGGREGATE, unit);
        }

        default Metric std() {
            return std("");
        }

        default Metric std(@Nonnull final String unit) {
            return metric(Stat.STD, Type.AGGREGATE, unit);
        }

        default Metric percentile(final double quantile) {
            return percentile(quantile, "");
        }

        default Metric percentile(final double quantile, @Nonnull final String unit) {
            return metric(Percentile.of(quantile), Type.AGGREGATE, unit);
        }

        default Metric percentile(final int percentile) {
            return percentile(percentile, "");
        }

        default Metric percentile(final int percentile, @Nonnull final String unit) {
            return metric(Percentile.of(percentile), Type.AGGREGATE, unit);
        }

        default Metric gauge() {
            return gauge("");
        }

        default Metric gauge(@Nonnull final String unit) {
            return metric(Stat.VALUE, Type.GAUGE, unit);
        }

        Metric metric(Stat stat, Type type, String unit);
    }

    class Builder implements KeyStageBuilder, TagsStageBuilder, MetaStageBuilder, MetricBuilder {
        private Key key;
        private Tags tags = Tags.empty();
        private Tags meta = Tags.empty();

        @Override
        public TagsStageBuilder key(@Nonnull final String key) {
            this.key = Key.of(key);
            return this;
        }

        @Override
        public TagsStageBuilder key(@Nonnull final Key key) {
            this.key = key;
            return this;
        }

        @Override
        public Builder empty() {
            return this;
        }

        @Override
        public MetaStageBuilder tag(@Nonnull final String key, @Nonnull final String value) {
            this.tags = Tags.of(key, value);
            return this;
        }

        @Override
        public MetaStageBuilder tags(@Nonnull final Tags tags) {
            this.tags = tags;
            return this;
        }

        @Override
        public MetricBuilder meta(@Nonnull final String key, @Nonnull final String value) {
            this.meta = Tags.of(key, value);
            return this;
        }

        @Override
        public MetricBuilder meta(@Nonnull final Tags meta) {
            this.meta = meta;
            return this;
        }

        @Override
        public Metric metric(final Stat stat, final Type type, final String unit) {
            return ImmutableMetric.builder()
                    .key(key)
                    .tags(tags)
                    .meta(meta)
                    .stat(stat)
                    .type(type)
                    .unit(unit)
                    .build();
        }
    }
}
