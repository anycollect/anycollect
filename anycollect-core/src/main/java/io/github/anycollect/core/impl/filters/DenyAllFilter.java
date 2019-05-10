package io.github.anycollect.core.impl.filters;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.github.anycollect.core.api.filter.Filter;
import io.github.anycollect.core.api.filter.FilterReply;
import io.github.anycollect.metric.frame.MetricFrame;

import javax.annotation.Nonnull;

@JsonTypeName("deny")
public final class DenyAllFilter implements Filter {
    @Override
    public FilterReply accept(@Nonnull final MetricFrame frame) {
        return FilterReply.DENY;
    }
}
