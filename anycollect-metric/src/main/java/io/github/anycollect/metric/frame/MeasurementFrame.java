package io.github.anycollect.metric.frame;

import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;

import javax.annotation.Nonnull;

public interface MeasurementFrame {
    @Nonnull
    Stat getStat();

    @Nonnull
    Type getType();

    @Nonnull
    String getUnit();
}
