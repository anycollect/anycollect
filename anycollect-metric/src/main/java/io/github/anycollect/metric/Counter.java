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
        CounterBuilder(@Nonnull final String value) {
            super(value);
        }

        @Override
        protected CounterBuilder self() {
            return this;
        }

        public Counter register(@Nonnull final MeterRegistry registry) {
            return registry.counter(new ImmutableMeterId(getTagsBuilder().build(), getMetaBuilder().build()));
        }
    }
}
