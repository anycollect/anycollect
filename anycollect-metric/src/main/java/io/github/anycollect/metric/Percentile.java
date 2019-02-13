package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;

import java.util.Objects;

@EqualsAndHashCode
public final class Percentile implements Stat {
    private final Stat stat;
    private final int num;

    @SuppressWarnings("checkstyle:magicnumber")
    public Percentile(final Stat stat, final double percentile) {
        Objects.requireNonNull(stat, "stat must not be null");
        if (percentile <= 0) {
            throw new IllegalArgumentException("percentile must be positive");
        }
        this.stat = stat;
        this.num = (int) ((int) (percentile * 1000) % 10 == 0 ? percentile * 100 : percentile * 1000);
    }

    public Percentile(final Stat stat, final int num) {
        Objects.requireNonNull(stat, "stat must not be null");
        if (num <= 0) {
            throw new IllegalArgumentException("percentile must be positive");
        }
        this.stat = stat;
        this.num = num;
    }

    public Stat getStat() {
        return stat;
    }

    @Override
    public StatType getType() {
        return StatType.PERCENTILE;
    }

    @Override
    public String getTagValue() {
        return stat.getTagValue() + "_" + num;
    }

    @Override
    public String toString() {
        return getTagValue();
    }
}
