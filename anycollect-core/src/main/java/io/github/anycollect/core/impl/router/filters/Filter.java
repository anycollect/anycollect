package io.github.anycollect.core.impl.router.filters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DenyKeyFilter.class, name = "denyKey"),
        @JsonSubTypes.Type(value = DenyAllFilter.class, name = "deny"),
        @JsonSubTypes.Type(value = AcceptKeyFilter.class, name = "acceptKey"),
        @JsonSubTypes.Type(value = AcceptAllFilter.class, name = "accept")
})
public interface Filter {
    FilterReply accept(@Nonnull Metric metric);
}
