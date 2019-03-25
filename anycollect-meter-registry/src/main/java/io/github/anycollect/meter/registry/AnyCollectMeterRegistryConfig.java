package io.github.anycollect.meter.registry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.anycollect.metric.Tags;
import org.immutables.value.Value;

import javax.annotation.Nonnull;

@Value.Immutable
@Value.Style(builder = "new", stagedBuilder = true, passAnnotations = {Nonnull.class})
@JsonDeserialize(builder = ImmutableAnyCollectMeterRegistryConfig.Builder.class)
public interface AnyCollectMeterRegistryConfig {
    static ImmutableAnyCollectMeterRegistryConfig.Builder builder() {
        return new ImmutableAnyCollectMeterRegistryConfig.Builder();
    }

    AnyCollectMeterRegistryConfig DEFAULT = new AnyCollectMeterRegistryConfig() {
    };

    @Nonnull
    @Value.Default
    default String globalPrefix() {
        return "";
    }

    @Nonnull
    @Value.Default
    default Tags commonTags() {
        return Tags.empty();
    }

    @Nonnull
    @Value.Default
    default Tags commonMeta() {
        return Tags.empty();
    }
}
