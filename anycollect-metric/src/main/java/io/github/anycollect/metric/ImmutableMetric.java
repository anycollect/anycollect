package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import java.util.Objects;

final class ImmutableMetric implements Metric {
    private final Key key;
    private final Tags tags;
    private final Tags meta;
    private final Stat stat;
    private final String unit;

    ImmutableMetric(@Nonnull final Key key,
                    @Nonnull final Tags tags,
                    @Nonnull final Tags meta,
                    @Nonnull final Stat stat,
                    @Nonnull final String unit) {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(tags, "tags must not be null");
        Objects.requireNonNull(meta, "meta must not be null");
        Objects.requireNonNull(stat, "stat must not be null");
        Objects.requireNonNull(unit, "unit must not be null");
        this.key = key;
        this.tags = tags;
        this.meta = meta;
        this.stat = stat;
        this.unit = unit;
    }

    @Nonnull
    @Override
    public Key getKey() {
        return key;
    }

    @Nonnull
    @Override
    public Tags getTags() {
        return tags;
    }

    @Nonnull
    @Override
    public Tags getMeta() {
        return meta;
    }

    @Nonnull
    @Override
    public Stat getStat() {
        return stat;
    }

    @Nonnull
    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Metric)) {
            return false;
        }
        return Metric.equals(this, (Metric) o);
    }

    @Override
    public int hashCode() {
        return Metric.hash(this);
    }

    @Override
    public String toString() {
        return Metric.toString(this);
    }
}
