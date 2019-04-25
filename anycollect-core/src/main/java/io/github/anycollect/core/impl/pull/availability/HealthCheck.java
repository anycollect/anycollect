package io.github.anycollect.core.impl.pull.availability;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.prepared.PreparedMetric;

import javax.annotation.Nonnull;

public final class HealthCheck implements Runnable {
    private final Dispatcher dispatcher;
    private final CheckingTarget<? extends Target<?>> checkingTarget;
    private final PreparedMetric state;

    public HealthCheck(@Nonnull final Dispatcher dispatcher,
                       @Nonnull final CheckingTarget<? extends Target<?>> checkingTarget) {
        this.dispatcher = dispatcher;
        this.checkingTarget = checkingTarget;
        Target<?> target = this.checkingTarget.get();
        this.state = Metric.prepare()
                .key("health.check")
                .tag("target.id", target.getId())
                .concatTags(target.getTags())
                .concatMeta(target.getMeta())
                .gauge()
                .build();
    }

    public void run() {
        Check check = checkingTarget.check();
        Metric health = state.compile(check.getTimestamp(), check.getHealth().getValue());
        dispatcher.dispatch(health);
    }
}
