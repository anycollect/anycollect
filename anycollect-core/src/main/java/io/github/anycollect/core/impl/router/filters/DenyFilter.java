package io.github.anycollect.core.impl.router.filters;

import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public final class DenyFilter implements Filter {
    private final Predicate<MetricFamily> predicate;

    public DenyFilter(@Nonnull final Predicate<MetricFamily> predicate) {
        this.predicate = predicate;
    }

    @Override
    public FilterReply accept(@Nonnull final MetricFamily metric) {
        return predicate.test(metric) ? FilterReply.DENY : FilterReply.NEUTRAL;
    }
}
