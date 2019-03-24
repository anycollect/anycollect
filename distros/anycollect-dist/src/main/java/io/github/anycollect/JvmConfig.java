package io.github.anycollect;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.anycollect.metric.Tags;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableJvmConfig.class)
@JsonDeserialize(as = ImmutableJvmConfig.class)
public interface JvmConfig {
    JvmConfig DEFAULT = new JvmConfig() { };

    static JvmConfig common(int period) {
        return new DefaultJvmConfig(period);
    }

    @Value.Default
    @JsonProperty("enabled")
    default boolean enabled() {
        return true;
    }

    @Value.Default
    @JsonProperty(value = "period", required = true)
    default int period() {
        return -1;
    }

    @Value.Default
    @JsonProperty("application")
    default String applicationName() {
        return "anycollect";
    }

    @Value.Default
    @JsonProperty("tags")
    default Tags tags() {
        return Tags.empty();
    }

    @Value.Default
    @JsonProperty("meta")
    default Tags meta() {
        return Tags.empty();
    }

    class DefaultJvmConfig implements JvmConfig {
        private final int period;

        DefaultJvmConfig(final int period) {
            this.period = period;
        }

        @Override
        public int period() {
            return period;
        }
    }
}
