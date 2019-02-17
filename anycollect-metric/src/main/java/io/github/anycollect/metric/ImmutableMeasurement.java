package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;

@Getter
@ToString
@EqualsAndHashCode
public final class ImmutableMeasurement implements Measurement {
    private final Stat stat;
    private final Type type;
    private final double value;

    public ImmutableMeasurement(@Nonnull final Stat stat,
                                @Nonnull final Type type,
                                final double value) {
        this.stat = stat;
        this.type = type;
        this.value = value;
    }
}
