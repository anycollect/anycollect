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
        return new Builder(Key.of(key));
    }

    static Builder key(@Nonnull final Key key) {
        return new Builder(key);
    }

    void record(long amount, @Nonnull TimeUnit timeUnit);

    void record(@Nonnull Runnable runnable);

    final class Builder extends BaseMeterBuilder<Builder> {
        private TimeUnit timeUnit = TimeUnit.NANOSECONDS;

        Builder(@Nonnull final Key key) {
            super(key);
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
