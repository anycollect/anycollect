package io.github.anycollect.readers.process.discovery.current;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.anycollect.metric.Tags;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableCurrentProcessDiscoveryConfig.class)
@JsonDeserialize(as = ImmutableCurrentProcessDiscoveryConfig.class)
public interface CurrentProcessDiscoveryConfig {
    static ImmutableCurrentProcessDiscoveryConfig.Builder builder() {
        return ImmutableCurrentProcessDiscoveryConfig.builder();
    }

    @Value.Default
    @JsonProperty("tags")
    default Tags tags() {
        return Tags.of("name", "anycollect");
    }
}
