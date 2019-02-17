package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

// TODO report metrics from result: waiting time, processing time, success count and so on
public final class CallbackToDispatcherAdapter<T extends Target<Q>, Q extends Query> implements ResultCallback<T, Q> {
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
        }
    }
}
