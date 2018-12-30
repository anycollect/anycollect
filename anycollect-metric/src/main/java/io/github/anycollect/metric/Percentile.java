package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class Percentile implements Stat {
    private final int num;

    public Percentile(final int num) {
        if (num <= 0) {
            throw new IllegalArgumentException("integer part must be positive");
        }
        this.num = num;
    }

    @Override
    public StatType getType() {
        return StatType.PERCENTILE;
    }

    @Override
    public String getTagValue() {
        return num + "_NUM";
    }

    @Override
    public String toString() {
        return getTagValue();
    }
}
