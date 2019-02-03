package io.github.anycollect.core.api.target;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;

public interface Target<Q extends Query> {
    String DEFAULT_LABEL = "default";

    @Nonnull
    default String getLabel() {
        return DEFAULT_LABEL;
    }

    List<Metric> execute(@Nonnull Q query) throws QueryException, ConnectionException;
}
