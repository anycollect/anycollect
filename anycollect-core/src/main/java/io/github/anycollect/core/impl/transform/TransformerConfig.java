package io.github.anycollect.core.impl.transform;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.anycollect.core.impl.filters.AcceptAllFilter;
import io.github.anycollect.core.impl.filters.Filter;
import io.github.anycollect.core.impl.transform.transformations.Transformation;
import org.immutables.value.Value;

import java.util.Collections;
import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableTransformerConfig.class)
@JsonDeserialize(as = ImmutableTransformerConfig.class)
public interface TransformerConfig {
    @Value.Default
    @JsonProperty("source")
    default MetricSourceAction metricSourceAction() {
        return MetricSourceAction.DROP;
    }

    @Value.Default
    @JsonProperty("filters")
    default List<Filter> filters() {
        return Collections.singletonList(new AcceptAllFilter());
    }

    @JsonProperty("transformations")
    List<Transformation> transformations();
}
