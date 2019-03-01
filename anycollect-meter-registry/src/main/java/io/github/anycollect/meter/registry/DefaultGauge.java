package io.github.anycollect.meter.registry;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.*;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.ToDoubleFunction;

public class DefaultGauge<T> extends AbstractMeter implements Gauge, Measurable {
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
    public List<MetricFamily> measure() {
        double value = this.value.applyAsDouble(obj);
        Measurement gauge = Measurement.gauge(value, getId().getUnit());
        return Collections.singletonList(MetricFamily.of(getId(), gauge, clock.wallTime()));
    }
}
