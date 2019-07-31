package io.github.anycollect.core.impl.filters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.github.anycollect.core.api.filter.Filter;
import io.github.anycollect.core.api.filter.FilterReply;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@JsonTypeName("acceptKey")
public final class AcceptKeyFilter implements Filter {
    private final AcceptFilter accept;

    @JsonCreator
    public AcceptKeyFilter(@JsonProperty("regexp") @Nullable final String regexp,
                           @JsonProperty("equals") @Nullable final String equals,
                           @JsonProperty("startsWith") @Nullable final String startsWith,
                           @JsonProperty("endsWith") @Nullable final String endsWith,
                           @JsonProperty("contains") @Nullable final String contains) {
        accept = new AcceptFilter(new MetricKeyPredicate(regexp, equals, startsWith, endsWith, contains));
    }


    @Override
    public FilterReply accept(@Nonnull final Metric metric) {
        return accept.accept(metric);
    }
}
