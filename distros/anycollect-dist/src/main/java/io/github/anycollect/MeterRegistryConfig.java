package io.github.anycollect;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.anycollect.meter.registry.AnyCollectMeterRegistryConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableMeterRegistryConfig.class)
@JsonDeserialize(as = ImmutableMeterRegistryConfig.class)
public interface MeterRegistryConfig {
    MeterRegistryConfig DEFAULT = new MeterRegistryConfig() { };

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

    @JsonUnwrapped
    @Value.Default
    default AnyCollectMeterRegistryConfig config() {
        return AnyCollectMeterRegistryConfig.DEFAULT;
    }
}
