package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import java.util.function.ToDoubleFunction;

public interface FunctionCounter {
    FunctionCounter NOOP = new FunctionCounter() { };

    static <T> FunctionCounterBuilder<T> make(@Nonnull final String key,
                                              @Nonnull final T obj,
                                              @Nonnull final ToDoubleFunction<T> value) {
        return new FunctionCounterBuilder<>(key, obj, value);
    }

    final class FunctionCounterBuilder<T> extends BaseMeterBuilder<FunctionCounterBuilder<T>> {
        private final T obj;
        private final ToDoubleFunction<T> value;

        FunctionCounterBuilder(@Nonnull final String key,
                               @Nonnull final T obj,
                               @Nonnull final ToDoubleFunction<T> value) {
            super(key);
            this.obj = obj;
            this.value = value;
        }

        public FunctionCounterBuilder(@Nonnull final T obj, @Nonnull final ToDoubleFunction<T> value,
                                      @Nonnull final String... keyParts) {
            super(keyParts);
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
