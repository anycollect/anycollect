package io.github.anycollect.core.impl.pull.availability;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@JsonDeserialize(builder = HealthChecksConfig.Builder.class)
public final class HealthChecksConfig {
    private static final int DEFAULT_GLOBAL_PERIOD_IN_SECONDS = 5;
    private static final int DEFAULT_GLOBAL_TIMEOUT_IN_SECONDS = 3;
    private final int globalPeriodInSeconds;
    private final int globalTimeoutInSeconds;
    private final List<HealthCheckConfig> checks;

    public static Builder builder() {
        return new Builder();
    }

    private HealthChecksConfig(final Builder builder) {
        this.globalPeriodInSeconds = builder.globalPeriodInSeconds;
        this.globalTimeoutInSeconds = builder.globalTimeoutInSeconds;
        this.checks = Collections.unmodifiableList(new ArrayList<>(builder.checks));
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private int globalPeriodInSeconds = DEFAULT_GLOBAL_PERIOD_IN_SECONDS;
        private int globalTimeoutInSeconds = DEFAULT_GLOBAL_TIMEOUT_IN_SECONDS;
        private List<HealthCheckConfig> checks = new ArrayList<>();

        @JsonProperty("period")
        public Builder periodInSeconds(final int globalPeriodInSeconds) {
            this.globalPeriodInSeconds = globalPeriodInSeconds;
            return this;
        }

        @JsonProperty("timeout")
        public Builder timeoutInSeconds(final int globalTimeoutInSeconds) {
            this.globalTimeoutInSeconds = globalPeriodInSeconds;
            return this;
        }

        @JsonProperty("checks")
        public Builder checks(final List<HealthCheckConfig> checks) {
            this.checks = checks;
            return this;
        }

        public HealthChecksConfig build() {
            return new HealthChecksConfig(this);
        }
    }
}
