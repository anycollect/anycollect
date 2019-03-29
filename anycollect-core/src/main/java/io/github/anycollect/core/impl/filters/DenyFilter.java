package io.github.anycollect.core.impl.filters;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public final class DenyFilter implements Filter {
    private final Predicate<Metric> predicate;

    public DenyFilter(@Nonnull final Predicate<Metric> predicate) {
        this.predicate = predicate;
    }

    @Override
    public FilterReply accept(@Nonnull final Metric metric) {
        return predicate.test(metric) ? FilterReply.DENY : FilterReply.NEUTRAL;
    }
}
