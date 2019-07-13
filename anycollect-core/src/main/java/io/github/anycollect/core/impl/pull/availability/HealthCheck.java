package io.github.anycollect.core.impl.pull.availability;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.metric.prepared.PreparedMetric;

import javax.annotation.Nonnull;

public final class HealthCheck implements Runnable {
    private final Dispatcher dispatcher;
    private final CheckingTarget<? extends Target> checkingTarget;
    private final PreparedMetric state;
    private final PreparedMetric up;
    private final PreparedMetric down;
    private final PreparedMetric unknown;
    private final long timeout;
    private final Clock clock;

    public HealthCheck(@Nonnull final Dispatcher dispatcher,
                       @Nonnull final CheckingTarget<? extends Target> checkingTarget,
                       @Nonnull final Tags tags,
                       @Nonnull final Tags meta,
                       final long timeout) {
        this(dispatcher, checkingTarget, tags, meta, timeout, Clock.getDefault());
    }

    HealthCheck(@Nonnull final Dispatcher dispatcher,
                @Nonnull final CheckingTarget<? extends Target> checkingTarget,
                @Nonnull final Tags tags,
                @Nonnull final Tags meta,
                final long timeout,
                @Nonnull final Clock clock) {
        this.dispatcher = dispatcher;
        this.checkingTarget = checkingTarget;
        this.timeout = timeout;
        Target target = this.checkingTarget.get();
        Tags resultTags = tags.concat(target.getTags());
        Tags resultMeta = Tags.of("target.id", target.getId())
                .concat(meta)
                .concat(target.getMeta());
        this.state = make("health.check", resultTags, resultMeta);
        this.up = make("instances.up", resultTags, resultMeta);
        this.down = make("instances.down", resultTags, resultMeta);
        this.unknown = make("instances.unknown", resultTags, resultMeta);
        this.clock = clock;
    }

    private static PreparedMetric make(final String key, final Tags tags, final Tags meta) {
        return Metric.prepare()
                .key(key)
                .concatTags(tags)
                .concatMeta(meta)
                .gauge()
                .build();
    }

    public void run() {
        Check check = checkingTarget.check();
        long now = clock.wallTime();
        Health health;
        if (check.getTimestamp() < now - timeout) {
            health = Health.UNKNOWN;
        } else {
            health = check.getHealth();
        }
        dispatcher.dispatch(state.compile(now, health.getStatusCode()));
        dispatcher.dispatch(up.compile(now, health == Health.PASSED ? 1 : 0));
        dispatcher.dispatch(down.compile(now, health == Health.FAILED ? 1 : 0));
        dispatcher.dispatch(unknown.compile(now, health == Health.UNKNOWN ? 1 : 0));
    }
}
