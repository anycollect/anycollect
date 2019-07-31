package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class MutableMetricImpl implements MutableMetric {
    private Key key;
    private Tags tags;
    private Tags meta;
    private Stat stat;
    private Type type;
    private String unit;

    MutableMetricImpl(@Nonnull final Metric metric) {
        this.key = metric.getKey();
        this.tags = metric.getTags();
        this.meta = metric.getMeta();
        this.stat = metric.getStat();
        this.type = metric.getType();
        this.unit = metric.getUnit();
    }

    @Override
    public MutableMetric withPrefix(@Nullable final String prefix) {
        if (prefix != null && !prefix.isEmpty()) {
            this.key = this.key.withPrefix(prefix);
        }
        return this;
    }

    @Override
    public MutableMetric frontTags(final Tags prefix) {
        this.tags = prefix.concat(this.tags);
        return this;
    }

    @Override
    public MutableMetric backTags(final Tags suffix) {
        this.tags = this.tags.concat(suffix);
        return this;
    }

    @Override
    public MutableMetric frontMeta(final Tags prefix) {
        this.meta = prefix.concat(this.meta);
        return this;
    }

    @Override
    public MutableMetric backMeta(final Tags suffix) {
        this.meta = this.meta.concat(suffix);
        return this;
    }

    @Override
    public MutableMetric removeTag(final String key) {
        this.tags = this.tags.remove(key);
        return this;
    }

    @Override
    public MutableMetric removeMeta(final String key) {
        this.meta = this.meta.remove(key);
        return this;
    }

    @Override
    public Metric commit() {
        return Metric.builder()
                .key(key)
                .tags(tags)
                .meta(meta)
                .metric(stat, type, unit);
    }
}
