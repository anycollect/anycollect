package io.github.anycollect.meter.api;

import io.github.anycollect.metric.Key;
import org.apiguardian.api.API;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

@API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
public interface Timer {
    Timer NOOP = new Timer() {
        @Override
        public void record(final long amount, @Nonnull final TimeUnit timeUnit) {

        }

        @Override
        public void record(@Nonnull final Runnable runnable) {
            runnable.run();
        }
    };

    static TimerBuilder key(@Nonnull final String key) {
        return new TimerBuilder(Key.of(key));
    }

    static TimerBuilder key(@Nonnull final Key key) {
        return new TimerBuilder(key);
    }

    void record(long amount, @Nonnull TimeUnit timeUnit);

    void record(@Nonnull Runnable runnable);

    final class TimerBuilder extends BaseMeterBuilder<TimerBuilder> {
        private TimeUnit timeUnit = TimeUnit.NANOSECONDS;

        TimerBuilder(@Nonnull final Key key) {
            super(key);
        }

        @Override
        protected TimerBuilder self() {
            return this;
        }

        public TimerBuilder unit(@Nonnull final TimeUnit timeUnit) {
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
