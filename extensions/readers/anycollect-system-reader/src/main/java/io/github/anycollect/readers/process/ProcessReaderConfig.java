package io.github.anycollect.readers.process;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableProcessReaderConfig.class)
@JsonDeserialize(as = ImmutableProcessReaderConfig.class)
public interface ProcessReaderConfig {
    static ImmutableProcessReaderConfig.Builder builder() {
        return ImmutableProcessReaderConfig.builder();
    }

    @Value.Default
    @JsonProperty("prefix")
    default String prefix() {
        return "";
    }

    @Value.Default
    @JsonProperty("memoryUsageKey")
    default String memoryUsageKey() {
        return "process.memory.usage";
    }

    @Value.Default
    @JsonProperty("cpuUsageKey")
    default String cpuUsageKey() {
        return "process.cpu.usage";
    }

    @JsonProperty(value = "period", required = true)
    int period();

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
