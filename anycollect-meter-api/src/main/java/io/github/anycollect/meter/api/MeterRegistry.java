package io.github.anycollect.meter.api;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

@API(since = "0.1.0", status = API.Status.INTERNAL)
public abstract class MeterRegistry {
    public static MeterRegistry noop() {
        return NoopMeterRegistry.INSTANCE;
    }

    @Nonnull
    protected abstract <T> Gauge gauge(@Nonnull MeterId id, @Nonnull T obj, @Nonnull ToDoubleFunction<T> value);

    @Nonnull
    protected abstract Counter counter(@Nonnull MeterId id);

    @Nonnull
    protected abstract <T> FunctionCounter counter(@Nonnull MeterId id, @Nonnull T obj, @Nonnull ToLongFunction<T> value);

    @Nonnull
    protected abstract Distribution distribution(@Nonnull MeterId id);

    @Nonnull
    protected abstract Timer timer(@Nonnull MeterId id, @Nonnull TimeUnit timeUnit);
}
