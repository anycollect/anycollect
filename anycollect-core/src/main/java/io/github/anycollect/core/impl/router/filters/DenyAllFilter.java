package io.github.anycollect.core.impl.router.filters;

import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;

public final class DenyAllFilter implements Filter {
    @Override
    public FilterReply accept(@Nonnull final MetricFamily metric) {
        return FilterReply.DENY;
    }
}
