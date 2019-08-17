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

    @Nonnull
    @Override
    public MutableMetric withPrefix(@Nullable final String prefix) {
        if (prefix != null && !prefix.isEmpty()) {
            this.key = this.key.withPrefix(prefix);
        }
        return this;
    }

    @Nonnull
    @Override
    public MutableMetric frontTags(@Nullable final Tags prefix) {
        if (prefix != null) {
            this.tags = prefix.concat(this.tags);
        }
        return this;
    }


    @Nonnull
    @Override
    public MutableMetric backTags(@Nullable final Tags suffix) {
        if (suffix != null) {
            this.tags = this.tags.concat(suffix);
        }
        return this;
    }

    @Nonnull
    @Override
    public MutableMetric frontMeta(@Nullable final Tags prefix) {
        if (prefix != null) {
            this.meta = prefix.concat(this.meta);
        }
        return this;
    }

    @Nonnull
    @Override
    public MutableMetric backMeta(@Nullable final Tags suffix) {
        if (suffix != null) {
            this.meta = this.meta.concat(suffix);
        }
        return this;
    }

    @Nonnull
    @Override
    public MutableMetric removeTag(@Nullable final Key key) {
        if (key != null) {
            this.tags = this.tags.remove(key);
        }
        return this;
    }

    @Nonnull
    @Override
    public MutableMetric removeMeta(@Nullable final Key key) {
        if (key != null) {
            this.meta = this.meta.remove(key);
        }
        return this;
    }

    @Nonnull
    @Override
    public Metric commit() {
        return Metric.builder()
                .key(key)
                .tags(tags)
                .meta(meta)
                .metric(stat, type, unit);
    }
}
