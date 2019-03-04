package io.github.anycollect.core.api.measurable;

import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Objects of this class is responsible to create {@link Metric} for given {@link Measurable}
 *
 * @param <T> - type of {@link Measurable}
 */
public interface Measurer<T extends Measurable> {
    @Nonnull
    Metric measure(@Nonnull T measurable, long timestamp) throws QueryException;

    @Nonnull
    Set<String> getPaths();
}
