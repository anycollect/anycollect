package io.github.anycollect.readers.jmx.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.anycollect.metric.Tags;
import org.immutables.value.Value;


@Value.Immutable
@JsonSerialize(as = ImmutableJvmMetricsConfig.class)
@JsonDeserialize(as = ImmutableJvmMetricsConfig.class)
public interface JvmMetricsConfig {
    static ImmutableJvmMetricsConfig.Builder builder() {
        return ImmutableJvmMetricsConfig.builder();
    }

    JvmMetricsConfig DEFAULT = new JvmMetricsConfig() { };

    @Value.Default
    @JsonProperty("prefix")
    default String prefix() {
        return "";
    }

    @Value.Default
    @JsonProperty("tags")
    default Tags tags() {
        return Tags.empty();
    }

    @Value.Default
    @JsonProperty("meta")
    default Tags meta() {
        return Tags.empty();
    }
}
