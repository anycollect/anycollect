package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.MetricFamily;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.metric.noop.NoopMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public final class CallbackToDispatcherAdapter<T extends Target<Q>, Q extends Query> implements ResultCallback<T, Q> {
    private static final Logger LOG = LoggerFactory.getLogger(CallbackToDispatcherAdapter.class);
    private final Dispatcher dispatcher;
    private final MeterRegistry registry;

    public CallbackToDispatcherAdapter(final Dispatcher dispatcher) {
        this(dispatcher, new NoopMeterRegistry());
    }

    public CallbackToDispatcherAdapter(@Nonnull final Dispatcher dispatcher,
                                       @Nonnull final MeterRegistry registry) {
        this.dispatcher = dispatcher;
        this.registry = registry;
    }

    @Override
    public void call(@Nonnull final Result<T, Q> result) {
        if (result.isSuccess()) {
            List<MetricFamily> metrics = result.getMetrics();
            Objects.requireNonNull(metrics);
            dispatcher.dispatch(metrics);
            LOG.debug("success: {}.execute({}) taken {}ms and produces {} metric families",
                    result.getTarget(),
                    result.getQuery(),
                    result.getProcessingTime(),
                    result.getMetrics().size());
            registry.counter("pull.jobs.succeeded", "jobs", Tags.of("target", result.getTarget().getId()))
                    .increment();
            registry.counter("pull.jobs.failed", "jobs", Tags.of("target", result.getTarget().getId()));
        } else {
            LOG.debug("failed: {}.execute({}) taken {}ms and failed",
                    result.getTarget(),
                    result.getQuery(),
                    result.getProcessingTime(),
                    result.getException());
            registry.counter("pull.jobs.failed", "jobs", Tags.of("target", result.getTarget().getId()))
                    .increment();
            registry.counter("pull.jobs.succeeded", "jobs", Tags.of("target", result.getTarget().getId()));
        }
    }
}
