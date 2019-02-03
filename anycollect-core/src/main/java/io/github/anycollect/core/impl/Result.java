package io.github.anycollect.core.impl;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.metric.Metric;

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
    private final List<Metric> metrics;
    private final Type type;
    private final Exception exception;
    private final long waitingTime;
    private final long processingTime;

    public static <T extends Target<Q>, Q extends Query> Result<T, Q> success(
            @Nonnull final T target,
            @Nonnull final Q query,
            @Nonnull final List<Metric> metrics,
            final long waitingTime,
            final long processingTime) {
        return new Result<>(target, query,
                new ArrayList<>(metrics),
                Type.SUCCESS,
                null,
                waitingTime,
                processingTime);
    }

    public static <T extends Target<Q>, Q extends Query> Result<T, Q> fail(
            @Nonnull final T target,
            @Nonnull final Q query,
            @Nonnull final Exception exception,
            final long waitingTime,
            final long processingTime) {
        return new Result<>(
                target,
                query,
                Collections.emptyList(),
                Type.FAILED,
                exception,
                waitingTime,
                processingTime
        );
    }

    private Result(@Nonnull final T target,
                   @Nonnull final Q query,
                   @Nonnull final List<Metric> metrics,
                   @Nonnull final Type type,
                   @Nullable final Exception exception,
                   final long waitingTime,
                   final long processingTime) {
        this.target = target;
        this.query = query;
        this.metrics = metrics;
        this.type = type;
        this.exception = exception;
        this.waitingTime = waitingTime;
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
    public List<Metric> getMetrics() {
        if (metrics == Collections.<Metric>emptyList()) {
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

    public long getWaitingTime() {
        return waitingTime;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public enum Type {
        SUCCESS, FAILED
    }
}
