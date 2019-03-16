package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public interface Timer {
    Timer NOOP = new Timer() {
        @Override
        public void record(final long amount, @Nonnull final TimeUnit timeUnit) {

        }

        @Override
        public void record(@Nonnull final Runnable runnable) {

        }
    };

    static Builder key(@Nonnull final String key) {
        return new Builder(key);
    }

    static Builder key(@Nonnull final String... keyParts) {
        return new Builder(keyParts);
    }

    void record(long amount, @Nonnull TimeUnit timeUnit);

    void record(@Nonnull Runnable runnable);

    final class Builder extends BaseMeterBuilder<Builder> {
        private TimeUnit timeUnit = TimeUnit.NANOSECONDS;

        Builder(@Nonnull final String value) {
            super(value);
        }

        public Builder(@Nonnull final String... keyParts) {
            super(keyParts);
        }

        @Override
        protected Builder self() {
            return this;
        }

        public Builder unit(@Nonnull final TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
            switch (timeUnit) {
                case NANOSECONDS:
                    super.unit("ns");
                    break;
                case MILLISECONDS:
                    super.unit("ms");
                    break;
                case SECONDS:
                    super.unit("s");
                    break;
                default:
                    super.unit(timeUnit.name().toLowerCase());
            }
            return this;
        }

        public Timer register(@Nonnull final MeterRegistry registry) {
            ImmutableMeterId id = new ImmutableMeterId(
                    getKey(), getUnit(),
                    getTagsBuilder().build(), getMetaBuilder().build());
            return registry.timer(id, timeUnit);
        }
    }
}
