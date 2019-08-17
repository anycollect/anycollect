package io.github.anycollect.meter.api;

import io.github.anycollect.metric.Key;
import org.apiguardian.api.API;

import javax.annotation.Nonnull;
import java.util.function.ToLongFunction;

@API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
public interface FunctionCounter {
    FunctionCounter NOOP = new FunctionCounter() { };

    static <T> FunctionCounterBuilder<T> make(@Nonnull final String key,
                                              @Nonnull final T obj,
                                              @Nonnull final ToLongFunction<T> value) {
        return new FunctionCounterBuilder<>(Key.of(key), obj, value);
    }

    final class FunctionCounterBuilder<T> extends BaseMeterBuilder<FunctionCounterBuilder<T>> {
        private final T obj;
        private final ToLongFunction<T> value;

        FunctionCounterBuilder(@Nonnull final Key key,
                               @Nonnull final T obj,
                               @Nonnull final ToLongFunction<T> value) {
            super(key);
            this.obj = obj;
            this.value = value;
        }

        @Override
        protected FunctionCounterBuilder<T> self() {
            return this;
        }

        public FunctionCounterBuilder<T> unit(@Nonnull final String unit) {
            return super.unit(unit);
        }

        public FunctionCounter register(@Nonnull final MeterRegistry registry) {
            ImmutableMeterId id = new ImmutableMeterId(getKey(), getUnit(),
                    getTagsBuilder().build(), getMetaBuilder().build());
            return registry.counter(id, obj, value);
        }
    }
}
