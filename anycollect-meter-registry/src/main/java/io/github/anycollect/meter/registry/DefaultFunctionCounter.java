package io.github.anycollect.meter.registry;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.*;

import javax.annotation.Nonnull;
import java.util.function.ToDoubleFunction;

public class DefaultFunctionCounter<T> extends AbstractMeter implements FunctionCounter {
    private final Clock clock;
    private final T obj;
    private final ToDoubleFunction<T> value;

    public DefaultFunctionCounter(@Nonnull final MeterId id,
                                  @Nonnull final Clock clock,
                                  @Nonnull final T obj,
                                  @Nonnull final ToDoubleFunction<T> value) {
        super(id);
        this.clock = clock;
        this.obj = obj;
        this.value = value;
    }

    @Nonnull
    @Override
    public MetricFamily measure() {
        return MetricFamily.of(
                getId(),
                Measurement.counter(value.applyAsDouble(obj), getId().getUnit()),
                clock.wallTime());
    }
}
