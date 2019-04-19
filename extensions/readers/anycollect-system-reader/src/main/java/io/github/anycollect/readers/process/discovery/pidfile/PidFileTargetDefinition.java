package io.github.anycollect.readers.process.discovery.pidfile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.anycollect.metric.Tags;
import org.immutables.value.Value;

import java.nio.file.Path;

@Value.Immutable
@JsonSerialize(as = ImmutablePidFileTargetDefinition.class)
@JsonDeserialize(as = ImmutablePidFileTargetDefinition.class)
public interface PidFileTargetDefinition {
    @JsonProperty(value = "file", required = true)
    Path file();

    @JsonProperty(value = "targetId", required = true)
    String targetId();

    @Value.Default
    @JsonProperty("tags")
    default Tags tags() {
        return Tags.empty();
    }
}
