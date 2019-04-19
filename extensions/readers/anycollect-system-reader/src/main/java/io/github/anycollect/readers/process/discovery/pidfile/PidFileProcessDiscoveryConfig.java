package io.github.anycollect.readers.process.discovery.pidfile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Collections;
import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutablePidFileProcessDiscoveryConfig.class)
@JsonDeserialize(as = ImmutablePidFileProcessDiscoveryConfig.class)
public interface PidFileProcessDiscoveryConfig {
    @Value.Default
    @JsonProperty("watch")
    default List<PidFileTargetDefinition> watch() {
        return Collections.emptyList();
    }
}
