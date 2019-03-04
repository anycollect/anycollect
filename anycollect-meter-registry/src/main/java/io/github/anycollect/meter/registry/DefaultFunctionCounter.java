package io.github.anycollect.meter.registry;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.*;
import io.github.anycollect.metric.prepared.PreparedMetric;
import lombok.Builder;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.ToDoubleFunction;

public class DefaultFunctionCounter<T> extends AbstractMeter implements FunctionCounter, Measurable {
    private final Clock clock;
    private final T obj;
    private final ToDoubleFunction<T> value;
    private final PreparedMetric preparedCounter;

    @Builder
    public DefaultFunctionCounter(@Nonnull final MeterId id,
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
        this.preparedCounter = Metric.prepare()
                .key(prefix, id.getKey())
                .concatTags(tags)
                .concatTags(id.getTags())
                .concatMeta(meta)
                .concatMeta(id.getMetaTags())
                .measurement(Stat.value(), Type.GAUGE, id.getUnit())
                .build();
    }

    @Nonnull
    @Override
    public List<Metric> measure() {
        return Collections.singletonList(preparedCounter.compile(clock.wallTime(), value.applyAsDouble(obj)));
    }
}
