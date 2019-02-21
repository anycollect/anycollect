package io.github.anycollect.core.impl.pull.availability;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.impl.pull.PullScheduler;
import io.github.anycollect.metric.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

public final class ServiceAvailabilityCheck<T extends Target<Q>, Q extends Query> implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceAvailabilityCheck.class);
    private final PullScheduler scheduler;
    private final Dispatcher dispatcher;
    private final Clock clock;
    private final String service;
    private final int timeoutInSeconds;
    private final int periodInSeconds;
    private final List<HealthCheck<T, Q>> checks;

    public static <T extends Target<Q>, Q extends Query> Builder<T, Q> builder() {
        return new Builder<>();
    }

    private ServiceAvailabilityCheck(@Nonnull final Builder<T, Q> builder) {
        this.scheduler = builder.scheduler;
        this.dispatcher = builder.dispatcher;
        this.clock = builder.clock;
        this.service = builder.service;
        this.timeoutInSeconds = builder.timeoutInSeconds;
        this.periodInSeconds = builder.periodInSeconds;
        this.checks = builder.targets.stream()
                .map(target -> new HealthCheck<>(target, builder.healthCheck)).collect(toList());
    }

    @Override
    public void run() {
        List<Future<Health>> futures = new ArrayList<>();
        for (HealthCheck<T, Q> check : checks) {
            futures.add(scheduler.check(check));
        }
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(timeoutInSeconds));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        final int desired = checks.size();
        int up = 0;
        int down = 0;
        int timeout = 0;
        for (Future<Health> future : futures) {
            if (!future.isDone()) {
                timeout++;
                continue;
            }
            Health health;
            try {
                health = future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                LOG.warn("could not get health check info, this should never happen", cause);
                down++;
                continue;
            }
            if (health == Health.PASSED) {
                up++;
            } else {
                down++;
            }
        }
        List<MetricFamily> families = new ArrayList<>();
        long timestamp = clock.wallTime();
        families.add(make("instances.desired", desired, timestamp));
        families.add(make("instances.up", up, timestamp));
        families.add(make("instances.down", down, timestamp));
        families.add(make("instances.timeout", timeout, timestamp));
        dispatcher.dispatch(families);
    }

    private MetricFamily make(final String key, final int value, final long timestamp) {
        return MetricFamily.of(key, Tags.of("service", service), Tags.empty(), instances(value), timestamp);
    }

    private static Measurement instances(final int value) {
        return new ImmutableMeasurement(Stat.value(), Type.GAUGE, "instances", value);
    }

    public int getPeriodInSeconds() {
        return periodInSeconds;
    }

    public static final class Builder<T extends Target<Q>, Q extends Query> {
        private PullScheduler scheduler;
        private Dispatcher dispatcher;
        private Clock clock;
        private String service;
        private int timeoutInSeconds;
        private int periodInSeconds;
        private List<T> targets;
        private Q healthCheck;

        public Builder<T, Q> scheduler(@Nonnull final PullScheduler scheduler) {
            this.scheduler = scheduler;
            return this;
        }

        public Builder<T, Q> dispatcher(@Nonnull final Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
            return this;
        }

        public Builder<T, Q> clock(@Nonnull final Clock clock) {
            this.clock = clock;
            return this;
        }

        public Builder<T, Q> service(@Nonnull final String service) {
            this.service = service;
            return this;
        }

        public Builder<T, Q> period(final int periodInSeconds) {
            this.periodInSeconds = periodInSeconds;
            return this;
        }

        public Builder<T, Q> timeout(final int timeoutInSeconds) {
            this.timeoutInSeconds = timeoutInSeconds;
            return this;
        }

        public Builder<T, Q> targets(@Nonnull final List<T> targets) {
            this.targets = targets;
            return this;
        }

        public Builder<T, Q> healthCheck(@Nonnull final Q healthCheck) {
            this.healthCheck = healthCheck;
            return this;
        }

        public ServiceAvailabilityCheck<T, Q> build() {
            return new ServiceAvailabilityCheck<>(this);
        }
    }
}
