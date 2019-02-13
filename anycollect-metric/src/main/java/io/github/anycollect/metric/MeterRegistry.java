package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public interface MeterRegistry {
    Counter counter(@Nonnull MeterId id);

    DistributionSummary summary(@Nonnull MeterId id, @Nonnull double... percentiles);
}
