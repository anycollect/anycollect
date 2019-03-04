package io.github.anycollect.core.impl.router.filters;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;

public final class DenyAllFilter implements Filter {
    @Override
    public FilterReply accept(@Nonnull final Metric metric) {
        return FilterReply.DENY;
    }
}
