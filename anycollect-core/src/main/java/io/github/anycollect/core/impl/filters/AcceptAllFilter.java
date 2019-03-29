package io.github.anycollect.core.impl.filters;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;

public final class AcceptAllFilter implements Filter {
    @Override
    public FilterReply accept(@Nonnull final Metric metric) {
        return FilterReply.ACCEPT;
    }
}
