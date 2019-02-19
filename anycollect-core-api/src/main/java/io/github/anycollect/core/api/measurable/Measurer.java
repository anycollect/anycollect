package io.github.anycollect.core.api.measurable;

import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import java.util.Set;

public interface Measurer<T extends Measurable> {
    MetricFamily measure(@Nonnull T obj, long timestamp)
            throws QueryException;

    Set<String> getPaths();
}
