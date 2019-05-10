package io.github.anycollect.writers.collectd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableCollectdConfig.class)
@JsonDeserialize(as = ImmutableCollectdConfig.class)
public interface CollectdConfig {
    List<MappingConfig> mappings();
}
