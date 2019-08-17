package io.github.anycollect.meter.impl;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.meter.api.Gauge;
import io.github.anycollect.meter.api.MeterId;
import io.github.anycollect.metric.*;
import lombok.Builder;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.ToDoubleFunction;

public class DefaultGauge<T> extends AbstractMeter implements Gauge, Meter {
    private final Clock clock;
    private final T obj;
    private final ToDoubleFunction<T> value;
    private final Metric id;

    @Builder
    public DefaultGauge(@Nonnull final MeterId id,
                        @Nonnull final T obj,
                        @Nonnull final ToDoubleFunction<T> value,
                        @Nonnull final String prefix,
                        @Nonnull final Tags tags,
                        @Nonnull final Tags meta,
                        @Nonnull final Clock clock) {
        super(id);
        this.clock = clock;
        this.obj = obj;
        this.value = value;
        this.id = Metric.builder()
                .key(id.getKey().withPrefix(prefix))
                .tags(tags.concat(id.getTags()))
                .meta(meta.concat(id.getMeta()))
                .gauge(id.getUnit());
    }

    @Nonnull
    @Override
    public List<Sample> measure() {
        double value = this.value.applyAsDouble(obj);
        return Collections.singletonList(id.sample(value, clock.wallTime()));
    }
}
