package io.github.anycollect;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.anycollect.metric.Tags;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableInternalMonitoringConfig.class)
@JsonDeserialize(as = ImmutableInternalMonitoringConfig.class)
public interface InternalMonitoringConfig {
    InternalMonitoringConfig DEFAULT = new InternalMonitoringConfig() { };

    @Value.Default
    @JsonProperty("period")
    default int period() {
        return 10;
    }

    @JsonProperty("prefix")
    @Value.Default
    default String prefix() {
        return "anycollect";
    }

    @JsonProperty("tags")
    @Value.Default
    default Tags tags() {
        return Tags.empty();
    }

    @JsonProperty("meta")
    @Value.Default
    default Tags meta() {
        return Tags.of("source", "internal");
    }

    @JsonProperty("jvm")
    @Value.Default
    default MetricConfig jvm() {
        return MetricConfig.ENABLED;
    }

    @JsonProperty("mem")
    @Value.Default
    default MetricConfig mem() {
        return MetricConfig.ENABLED;
    }

    @JsonProperty("cpu")
    @Value.Default
    default MetricConfig cpu() {
        return MetricConfig.ENABLED;
    }

    @JsonProperty("logic")
    @Value.Default
    default MetricConfig logic() {
        return MetricConfig.ENABLED;
    }
}
