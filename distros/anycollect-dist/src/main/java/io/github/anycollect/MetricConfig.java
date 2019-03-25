package io.github.anycollect;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableMetricConfig.class)
@JsonDeserialize(as = ImmutableMetricConfig.class)
public interface MetricConfig {
    MetricConfig ENABLED = new MetricConfig() { };

    @Value.Default
    @JsonProperty("enabled")
    default boolean enabled() {
        return true;
    }

    @Value.Default
    @JsonProperty("period")
    default int period() {
        return -1;
    }
}
