package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public interface Counter extends Meter {
    static Builder key(@Nonnull final String key) {
        return new Builder(key);
    }

    default void increment() {
        increment(1.0);
    }

    void increment(double amount);

    class Builder {
        private final ImmutableMeterId.Builder idBuilder = new ImmutableMeterId.Builder();

        public Builder(@Nonnull final String key) {
            idBuilder.key(key);
        }

        public Builder unit(@Nonnull final String value) {
            idBuilder.unit(value);
            return this;
        }

        public Builder tags(@Nonnull final String key, @Nonnull final String value) {
            idBuilder.tag(key, value);
            return this;
        }

        public Builder meta(@Nonnull final String key, @Nonnull final String value) {
            idBuilder.meta(key, value);
            return this;
        }

        public Builder concatTags(@Nonnull final Tags addition) {
            idBuilder.concatTags(addition);
            return this;
        }

        public Builder concatMeta(@Nonnull final Tags addition) {
            idBuilder.concatMeta(addition);
            return this;
        }

        public Counter register(@Nonnull final MeterRegistry registry) {
            return registry.counter(idBuilder.build());
        }
    }
}
