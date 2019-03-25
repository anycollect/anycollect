package io.github.anycollect.core.impl.self;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableSelfDiscoveryConfig.class)
@JsonDeserialize(as = ImmutableSelfDiscoveryConfig.class)
public interface SelfDiscoveryConfig {
    static ImmutableSelfDiscoveryConfig.Builder builder() {
        return ImmutableSelfDiscoveryConfig.builder();
    }

    @Value.Default
    @JsonProperty("targetId")
    default String targetId() {
        return "anycollect-self";
    }
}
