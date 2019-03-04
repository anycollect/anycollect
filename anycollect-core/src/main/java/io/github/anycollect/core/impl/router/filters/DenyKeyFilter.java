package io.github.anycollect.core.impl.router.filters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class DenyKeyFilter implements Filter {
    private final DenyFilter deny;

    @JsonCreator
    public DenyKeyFilter(@JsonProperty("regexp") @Nullable final String regexp,
                         @JsonProperty("startsWith") @Nullable final String startsWith,
                         @JsonProperty("endsWith") @Nullable final String endsWith,
                         @JsonProperty("contains") @Nullable final String contains) {
        deny = new DenyFilter(new MetricKeyPredicate(regexp, startsWith, endsWith, contains));
    }

    @Override
    public FilterReply accept(@Nonnull final Metric metric) {
        return deny.accept(metric);
    }
}
