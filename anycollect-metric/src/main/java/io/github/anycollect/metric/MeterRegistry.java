package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public interface MeterRegistry {
    Counter counter(@Nonnull MeterId id);

    Distribution distribution(@Nonnull MeterId id);
}
