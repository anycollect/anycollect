package io.github.anycollect.meter.registry;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.*;
import lombok.Builder;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.ToLongFunction;

public class DefaultFunctionCounter<T> extends AbstractMeter implements FunctionCounter, Meter {
    private final Clock clock;
    private final T obj;
    private final ToLongFunction<T> value;
    private final Metric id;

    @Builder
    public DefaultFunctionCounter(@Nonnull final MeterId id,
                                  @Nonnull final T obj,
                                  @Nonnull final ToLongFunction<T> value,
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
                .meta(meta.concat(id.getMetaTags()))
                .counter(id.getUnit());
    }

    @Nonnull
    @Override
    public List<Sample> measure() {
        return Collections.singletonList(id.sample(value.applyAsLong(obj), clock.wallTime()));
    }
}
