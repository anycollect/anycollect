package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Immutable
public final class Result<T extends Target<Q>, Q extends Query> {
    private final T target;
    private final Q query;
    private final List<MetricFamily> metrics;
    private final Type type;
    private final Exception exception;
    private final long processingTime;

    public static <T extends Target<Q>, Q extends Query> Result<T, Q> success(
            @Nonnull final T target,
            @Nonnull final Q query,
            @Nonnull final List<MetricFamily> metrics,
            final long processingTime) {
        return new Result<>(target, query,
                new ArrayList<>(metrics),
                Type.SUCCESS,
                null,
                processingTime);
    }

    public static <T extends Target<Q>, Q extends Query> Result<T, Q> fail(
            @Nonnull final T target,
            @Nonnull final Q query,
            @Nonnull final Exception exception,
            final long processingTime) {
        return new Result<>(
                target,
                query,
                Collections.emptyList(),
                Type.FAILED,
                exception,
                processingTime
        );
    }

    private Result(@Nonnull final T target,
                   @Nonnull final Q query,
                   @Nonnull final List<MetricFamily> metrics,
                   @Nonnull final Type type,
                   @Nullable final Exception exception,
                   final long processingTime) {
        this.target = target;
        this.query = query;
        this.metrics = metrics;
        this.type = type;
        this.exception = exception;
        this.processingTime = processingTime;
    }

    @Nonnull
    public Q getQuery() {
        return query;
    }

    @Nonnull
    public T getTarget() {
        return target;
    }

    @Nonnull
    public List<MetricFamily> getMetrics() {
        if (metrics == Collections.<MetricFamily>emptyList()) {
            return metrics;
        }
        return Collections.unmodifiableList(metrics);
    }

    public boolean isSuccess() {
        return type == Type.SUCCESS;
    }

    public boolean isFailed() {
        return type == Type.FAILED;
    }

    @Nonnull
    public Optional<Exception> getException() {
        return Optional.ofNullable(exception);
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public enum Type {
        SUCCESS, FAILED
    }
}
