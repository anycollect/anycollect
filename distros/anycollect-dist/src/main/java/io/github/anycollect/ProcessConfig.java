package io.github.anycollect;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.anycollect.metric.Tags;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableProcessConfig.class)
@JsonDeserialize(as = ImmutableProcessConfig.class)
public interface ProcessConfig {
    ProcessConfig DEFAULT = new ProcessConfig() {
    };

    @JsonProperty("tags")
    @Value.Default
    default Tags tags() {
        return Tags.empty();
    }

    @Value.Default
    @JsonProperty("period")
    default int period() {
        return -1;
    }

    @Value.Default
    @JsonProperty("cpu")
    default boolean collectCpuUsage() {
        return true;
    }

    @Value.Default
    @JsonProperty("mem")
    default boolean collectMemoryUsage() {
        return true;
    }
}
