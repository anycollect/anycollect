package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

public interface MeterRegistry {
    @Nonnull
    <T> Gauge gauge(@Nonnull MeterId id, @Nonnull T obj, @Nonnull ToDoubleFunction<T> value);

    @Nonnull
    Counter counter(@Nonnull MeterId id);

    @Nonnull
    <T> FunctionCounter counter(@Nonnull MeterId id, @Nonnull T obj, @Nonnull ToDoubleFunction<T> value);

    @Nonnull
    Distribution distribution(@Nonnull MeterId id);

    List<MetricFamily> measure(@Nonnull Predicate<MeterId> filter);
}
