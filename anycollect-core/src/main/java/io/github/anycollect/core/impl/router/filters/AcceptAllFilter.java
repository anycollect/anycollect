package io.github.anycollect.core.impl.router.filters;

import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;

public class AcceptAllFilter implements Filter {
    @Override
    public FilterReply accept(@Nonnull final MetricFamily metric) {
        return FilterReply.ACCEPT;
    }
}
