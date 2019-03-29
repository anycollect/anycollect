package io.github.anycollect.core.impl.filters;

import io.github.anycollect.metric.frame.MetricFrame;

import javax.annotation.Nonnull;

public final class DenyAllFilter implements Filter {
    @Override
    public FilterReply accept(@Nonnull final MetricFrame frame) {
        return FilterReply.DENY;
    }
}
