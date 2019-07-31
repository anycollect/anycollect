package io.github.anycollect.core.api.filter;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
public interface Filter {
    FilterReply accept(@Nonnull Metric metric);
}
