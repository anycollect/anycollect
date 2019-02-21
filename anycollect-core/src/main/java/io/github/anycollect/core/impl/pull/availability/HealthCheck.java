package io.github.anycollect.core.impl.pull.availability;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;

import javax.annotation.Nonnull;
import java.util.concurrent.Callable;

public final class HealthCheck<T extends Target<Q>, Q extends Query> implements Callable<Health> {
    private final T target;
    private final Q healthCheck;

    public HealthCheck(@Nonnull final T target, @Nonnull final Q healthCheck) {
        this.target = target;
        this.healthCheck = healthCheck;
    }

    @Override
    public Health call() {
        try {
            target.execute(healthCheck);
            return Health.PASSED;
        } catch (QueryException | ConnectionException | RuntimeException e) {
            return Health.FAILED;
        }
    }

    @Nonnull
    public T getTarget() {
        return target;
    }
}
