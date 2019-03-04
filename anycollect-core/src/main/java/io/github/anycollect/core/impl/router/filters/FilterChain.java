package io.github.anycollect.core.impl.router.filters;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;

public final class FilterChain implements Filter {
    private final List<Filter> chain;

    public FilterChain(@Nonnull final List<Filter> chain) {
        this.chain = chain;
    }

    @Override
    public Metric map(@Nonnull final Metric metric) {
        Metric result = metric;
        for (Filter filter : chain) {
            result = filter.map(metric);
        }
        return result;
    }

    @Override
    public FilterReply accept(@Nonnull final Metric metric) {
        for (Filter filter : chain) {
            if (filter.accept(metric) == FilterReply.ACCEPT) {
                return FilterReply.ACCEPT;
            }
            if (filter.accept(metric) == FilterReply.DENY) {
                return FilterReply.DENY;
            }
        }
        return FilterReply.NEUTRAL;
    }
}
