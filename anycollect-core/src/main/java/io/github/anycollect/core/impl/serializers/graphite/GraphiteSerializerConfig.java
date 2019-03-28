package io.github.anycollect.core.impl.serializers.graphite;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableGraphiteSerializerConfig.class)
@JsonDeserialize(as = ImmutableGraphiteSerializerConfig.class)
public interface GraphiteSerializerConfig {
    GraphiteSerializerConfig DEFAULT = new GraphiteSerializerConfig() { };

    @Value.Default
    @JsonProperty("prefix")
    default String prefix() {
        return "";
    }

    @Value.Default
    @JsonProperty("tagSupport")
    default boolean tagSupport() {
        return true;
    }
}
