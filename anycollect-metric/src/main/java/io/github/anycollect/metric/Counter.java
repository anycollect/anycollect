package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public interface Counter extends Meter {
    static CounterBuilder key(@Nonnull final String key) {
        return new CounterBuilder(key);
    }

    default void increment() {
        increment(1.0);
    }

    void increment(double amount);


    final class CounterBuilder extends BaseMeterBuilder<CounterBuilder> {
        CounterBuilder(@Nonnull final String key) {
            super(key);
        }

        @Override
        protected CounterBuilder self() {
            return this;
        }

        public CounterBuilder unit(@Nonnull final String unit) {
            return super.unit(unit);
        }

        public Counter register(@Nonnull final MeterRegistry registry) {
            ImmutableMeterId id = new ImmutableMeterId(getKey(), getUnit(),
                    getTagsBuilder().build(), getMetaBuilder().build());
            return registry.counter(id);
        }
    }
}
