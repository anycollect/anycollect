package io.github.anycollect.core.impl.pull.availability;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.internal.State;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.impl.pull.PullScheduler;
import io.github.anycollect.core.impl.scheduler.Cancellation;
import io.github.anycollect.core.impl.scheduler.Scheduler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class HealthCheckerImpl<T extends Target<Q>, Q extends Query> implements HealthChecker<T, Q> {
    private final PullScheduler pullScheduler;
    private final Dispatcher dispatcher;
    private final Clock clock;
    private final HealthChecksConfig config;
    private final Q healthCheck;
    private final Scheduler healthCheckScheduler;
    private final Cancellation healthCheckingCancellation;


    public HealthCheckerImpl(@Nonnull final PullScheduler pullScheduler,
                             @Nonnull final Dispatcher dispatcher,
                             @Nonnull final Clock clock,
                             @Nonnull final Scheduler healthCheckScheduler,
                             @Nonnull final HealthChecksConfig config,
                             @Nonnull final Q healthCheck) {
        this.pullScheduler = pullScheduler;
        this.dispatcher = dispatcher;
        this.clock = clock;
        this.healthCheckScheduler = healthCheckScheduler;
        this.config = config;
        this.healthCheck = healthCheck;
        this.healthCheckingCancellation = () -> { };
    }

    @Override
    public void stop() {
        healthCheckingCancellation.cancel();
    }

    @Override
    public void update(@Nonnull final State<T, Q> state) {
        healthCheckingCancellation.cancel();
        Map<HealthCheckConfig, List<T>> configs = new HashMap<>();
        for (T target : state.getTargets()) {
            String targetId = target.getId();
            for (HealthCheckConfig healthCheckConfig : config.getChecks()) {
                if (healthCheckConfig.getTargetId().matcher(targetId).matches()) {
                    configs.putIfAbsent(healthCheckConfig, new ArrayList<>());
                    configs.get(healthCheckConfig).add(target);
                }
            }
        }
        List<ServiceAvailabilityCheck<T, Q>> serviceChecks = new ArrayList<>();
        for (Map.Entry<HealthCheckConfig, List<T>> entry : configs.entrySet()) {
            HealthCheckConfig healthCheckConfig = entry.getKey();
            List<T> targets = entry.getValue();
            int period = healthCheckConfig.getPeriodInSeconds();
            if (period == 0) {
                period = config.getGlobalPeriodInSeconds();
            }
            int timeout = healthCheckConfig.getTimeoutInSeconds();
            if (timeout == 0) {
                timeout = config.getGlobalTimeoutInSeconds();
            }
            String service = healthCheckConfig.getService();
            ServiceAvailabilityCheck<T, Q> serviceCheck = ServiceAvailabilityCheck.<T, Q>builder()
                    .scheduler(pullScheduler)
                    .dispatcher(dispatcher)
                    .clock(clock)
                    .service(service)
                    .period(period)
                    .timeout(timeout)
                    .targets(targets)
                    .healthCheck(healthCheck)
                    .build();
            serviceChecks.add(serviceCheck);
        }
        for (ServiceAvailabilityCheck<T, Q> serviceCheck : serviceChecks) {
            healthCheckScheduler.scheduleAtFixedRate(serviceCheck, serviceCheck.getPeriodInSeconds(), TimeUnit.SECONDS);
        }
    }
}
