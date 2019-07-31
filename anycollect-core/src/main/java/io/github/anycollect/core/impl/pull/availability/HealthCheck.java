package io.github.anycollect.core.impl.pull.availability;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;

public final class HealthCheck implements Runnable {
    private final Dispatcher dispatcher;
    private final CheckingTarget<? extends Target> checkingTarget;
    private final Metric state;
    private final Metric up;
    private final Metric down;
    private final Metric unknown;
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
        this.up = make("instances/up", resultTags, resultMeta);
        this.down = make("instances/down", resultTags, resultMeta);
        this.unknown = make("instances/unknown", resultTags, resultMeta);
        this.clock = clock;
    }

    private static Metric make(final String key, final Tags tags, final Tags meta) {
        return Metric.builder()
                .key(key)
                .tags(tags)
                .meta(meta)
                .gauge();
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
        dispatcher.dispatch(state.sample(health.getStatusCode(), now));
        dispatcher.dispatch(up.sample(health == Health.PASSED ? 1 : 0, now));
        dispatcher.dispatch(down.sample(health == Health.FAILED ? 1 : 0, now));
        dispatcher.dispatch(unknown.sample(health == Health.UNKNOWN ? 1 : 0, now));
    }
}
