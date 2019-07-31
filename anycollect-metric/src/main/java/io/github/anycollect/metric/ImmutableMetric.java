package io.github.anycollect.metric;

import lombok.Builder;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;

@Builder
@EqualsAndHashCode(exclude = "meta")
public final class ImmutableMetric implements Metric {
    private final Key key;
    private final Tags tags;
    private final Tags meta;
    private final Stat stat;
    private final Type type;
    private final String unit;

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
    public Type getType() {
        return type;
    }

    @Nonnull
    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return getKey() + ";"
                + (!getTags().isEmpty() ? getTags() + ";" : "")
                + getStat() + "[" + getType() + "]"
                + (getUnit().isEmpty() ? "" : "(" + getUnit() + ")");
    }
}
