package io.github.anycollect;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableInternalMonitoringConfig.class)
@JsonDeserialize(as = ImmutableInternalMonitoringConfig.class)
public interface InternalMonitoringConfig {
    static InternalMonitoringConfig common(int period) {
        return new DefaultInternalMonitoringConfig(period);
    }

    @JsonProperty("period")
    @Value.Default
    default int period() {
        return 10;
    }

    @JsonProperty("jvm")
    @Value.Default
    default JvmConfig jvm() {
        return JvmConfig.DEFAULT;
    }

    @JsonProperty("registry")
    @Value.Default
    default MeterRegistryConfig registry() {
        return MeterRegistryConfig.DEFAULT;
    }

    @JsonProperty("process")
    @Value.Default
    default ProcessConfig process() {
        return ProcessConfig.DEFAULT;
    }

    class DefaultInternalMonitoringConfig implements InternalMonitoringConfig {
        private final int period;

        public DefaultInternalMonitoringConfig(final int period) {
            this.period = period;
        }

        @Override
        public int period() {
            return period;
        }

        @Override
        public JvmConfig jvm() {
            return JvmConfig.common(period);
        }
    }
}
