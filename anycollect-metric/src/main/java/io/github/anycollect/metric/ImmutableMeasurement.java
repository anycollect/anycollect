package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;

@Getter
@EqualsAndHashCode
public final class ImmutableMeasurement implements Measurement {
    private final Stat stat;
    private final Type type;
    private final String unit;
    private final double value;

    public ImmutableMeasurement(@Nonnull final Stat stat,
                                @Nonnull final Type type,
                                @Nonnull final String unit,
                                final double value) {
        this.stat = stat;
        this.type = type;
        this.unit = unit;
        this.value = value;
    }

    @Override
    public String toString() {
        return stat + "[" + type + "]" + "=" + value + "(" + unit + ")";
    }
}
