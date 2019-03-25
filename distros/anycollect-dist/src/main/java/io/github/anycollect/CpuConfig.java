package io.github.anycollect;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.immutables.value.Value;

@Value.Immutable
public interface CpuConfig {
    @Value.Default
    @JsonProperty("enabled")
    default boolean enabled() {
        return true;
    }
}
