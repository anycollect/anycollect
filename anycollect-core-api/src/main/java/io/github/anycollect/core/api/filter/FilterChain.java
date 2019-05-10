package io.github.anycollect.core.api.filter;

import io.github.anycollect.metric.frame.MetricFrame;

import javax.annotation.Nonnull;
import java.util.List;

public final class FilterChain implements Filter {
    private final List<Filter> chain;

    public FilterChain(@Nonnull final List<Filter> chain) {
        this.chain = chain;
    }

    @Override
    public FilterReply accept(@Nonnull final MetricFrame frame) {
        for (Filter filter : chain) {
            if (filter.accept(frame) == FilterReply.ACCEPT) {
                return FilterReply.ACCEPT;
            }
            if (filter.accept(frame) == FilterReply.DENY) {
                return FilterReply.DENY;
            }
        }
        return FilterReply.NEUTRAL;
    }
}
