package io.github.anycollect.core.api.measurable;

import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Objects of this class is responsible to create {@link MetricFamily} for given {@link Measurable}
 *
 * @param <T> - type of {@link Measurable}
 */
public interface Measurer<T extends Measurable> {
    @Nonnull
    MetricFamily measure(@Nonnull T measurable, long timestamp) throws QueryException;

    @Nonnull
    Set<String> getPaths();
}
