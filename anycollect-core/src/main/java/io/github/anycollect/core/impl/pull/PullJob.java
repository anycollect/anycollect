package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.MetricFamily;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public final class PullJob<T extends Target<Q>, Q extends Query> implements Runnable {
    @Getter
    private final T target;
    @Getter
    private final Q query;
    private final ResultCallback<T, Q> callback;
    private final Clock clock;

    public PullJob(@Nonnull final T target,
                   @Nonnull final Q query,
                   @Nonnull final ResultCallback<T, Q> callback,
                   @Nonnull final Clock clock) {
        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(query, "query must not be null");
        Objects.requireNonNull(callback, "callback must not be null");
        this.target = target;
        this.query = query;
        this.callback = callback;
        this.clock = clock;
    }

    @Override
    public void run() {
        long start = clock.wallTime();
        try {
            List<MetricFamily> metrics = target.execute(query);
            callback.call(Result.success(target, query, metrics, clock.wallTime() - start));
        } catch (QueryException | ConnectionException | RuntimeException e) {
            callback.call(Result.fail(target, query, e, clock.wallTime() - start));
        }
    }

    @Override
    public String toString() {
        return target.getId() + ".execute(" + query.getId() + ")";
    }
}
