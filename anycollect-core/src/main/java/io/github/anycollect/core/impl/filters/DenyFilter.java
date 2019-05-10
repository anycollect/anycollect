package io.github.anycollect.core.impl.filters;

import io.github.anycollect.core.api.filter.Filter;
import io.github.anycollect.core.api.filter.FilterReply;
import io.github.anycollect.metric.frame.MetricFrame;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public final class DenyFilter implements Filter {
    private final Predicate<MetricFrame> predicate;

    public DenyFilter(@Nonnull final Predicate<MetricFrame> predicate) {
        this.predicate = predicate;
    }

    @Override
    public FilterReply accept(@Nonnull final MetricFrame frame) {
        return predicate.test(frame) ? FilterReply.DENY : FilterReply.NEUTRAL;
    }
}
