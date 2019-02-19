package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.metric.MetricFamily;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

// TODO report metrics from result: waiting time, processing time, success count and so on
public final class CallbackToDispatcherAdapter<T extends Target<Q>, Q extends Query> implements ResultCallback<T, Q> {
    private static final Logger LOG = LoggerFactory.getLogger(CallbackToDispatcherAdapter.class);
    private final Dispatcher dispatcher;

    public CallbackToDispatcherAdapter(final Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
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
        } else {
            LOG.debug("failed: {}.execute({}) taken {}ms and failed",
                    result.getTarget(),
                    result.getQuery(),
                    result.getProcessingTime(),
                    result.getException());
        }
    }
}
