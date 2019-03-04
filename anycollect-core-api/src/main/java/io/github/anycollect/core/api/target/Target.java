package io.github.anycollect.core.api.target;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.MetricFamily;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import java.util.List;

public interface Target<Q extends Query> {
    /**
     * Instance id for application that can unique identify the target.
     * It can be for example in "127.0.0.1:8080" or "users-123" formats.
     * This value should be unique across all monitored by anycollect targets.
     *
     * @return instance id
     */
    @Nonnull
    String getId();

    @Nonnull
    Tags getTags();

    /**
     * Override {@link Target#execute(Query, Dispatcher)} because that method can be implemented much more efficient
     */
    @Deprecated
    List<MetricFamily> execute(@Nonnull Q query) throws QueryException, ConnectionException;

    default void execute(@Nonnull Q query, @Nonnull Dispatcher dispatcher) throws QueryException, ConnectionException {
        List<MetricFamily> metrics = execute(query);
        for (MetricFamily metric : metrics) {
            dispatcher.dispatch(metric);
        }
    }
}
