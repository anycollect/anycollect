package io.github.anycollect.writers.collectd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.anycollect.core.api.filter.Filter;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableMappingConfig.class)
@JsonDeserialize(as = ImmutableMappingConfig.class)
public interface MappingConfig {
    List<Filter> filters();

    String host();

    String plugin();

    String pluginInstance();

    String type();

    String typeInstance();
}
