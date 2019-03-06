package io.github.anycollect.kv.consul;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableConsulConfig.class)
@JsonDeserialize(as = ImmutableConsulConfig.class)
public interface ConsulConfig {
    String host();

    int port();
}
