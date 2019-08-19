package io.github.anycollect.core.impl.serializers.graphite;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.anycollect.metric.Key;
import io.github.anycollect.metric.Tags;
import org.immutables.value.Value;

import java.util.Collections;
import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableGraphiteSerializerConfig.class)
@JsonDeserialize(as = ImmutableGraphiteSerializerConfig.class)
public interface GraphiteSerializerConfig {
    GraphiteSerializerConfig DEFAULT = new GraphiteSerializerConfig() { };

    static ImmutableGraphiteSerializerConfig.Builder builder() {
        return ImmutableGraphiteSerializerConfig.builder();
    }

    @Value.Default
    @JsonProperty("prefix")
    default Key prefix() {
        return Key.empty();
    }

    @Value.Default
    @JsonProperty("tagSupport")
    default boolean tagSupport() {
        return true;
    }

    @Value.Default
    @JsonProperty("tagsAsPrefix")
    default List<Key> tagsAsPrefix() {
        return Collections.emptyList();
    }

    @Value.Default
    @JsonProperty("tags")
    default Tags tags() {
        return Tags.empty();
    }
}
