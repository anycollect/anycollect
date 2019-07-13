package io.github.anycollect.core.api.internal;

import io.github.anycollect.metric.Tags;
import org.immutables.value.Value;

import javax.annotation.Nonnull;

// TODO remove
@Value.Immutable
@Value.Style(builder = "new", stagedBuilder = true, passAnnotations = {Nonnull.class})
public interface HealthCheckConfig {
    HealthCheckConfig DISABLED = new HealthCheckConfig() {
        @Nonnull
        @Override
        public Tags tags() {
            return Tags.empty();
        }

        @Override
        public boolean enabled() {
            return false;
        }
    };

    static ImmutableHealthCheckConfig.TagsBuildStage builder() {
        return new ImmutableHealthCheckConfig.Builder();
    }

    @Nonnull
    Tags tags();

    @Nonnull
    @Value.Default
    default Tags meta() {
        return Tags.empty();
    }

    @Value.Default
    default int period() {
        return -1;
    }

    @Value.Default
    default boolean enabled() {
        return true;
    }
}
