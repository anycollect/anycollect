package io.github.anycollect.meter.registry;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.*;

import javax.annotation.Nonnull;
import java.util.function.ToDoubleFunction;

public class DefaultGauge<T> extends AbstractMeter implements Gauge {
    private final Clock clock;
    private final T obj;
    private final ToDoubleFunction<T> value;

    public DefaultGauge(@Nonnull final MeterId id,
                        @Nonnull final T obj,
                        @Nonnull final ToDoubleFunction<T> value,
                        @Nonnull final Clock clock) {
        super(id);
        this.clock = clock;
        this.obj = obj;
        this.value = value;
    }

    @Nonnull
    @Override
    public MetricFamily measure() {
        double value = this.value.applyAsDouble(obj);
        return MetricFamily.of(getId(), Measurement.gauge(value, getId().getUnit()), clock.wallTime());
    }
}
