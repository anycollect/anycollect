package io.github.anycollect.core.impl.pull.availability;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

@Getter
@JsonDeserialize(builder = HealthCheckConfig.Builder.class)
public final class HealthCheckConfig {
    private final Pattern targetId;
    private final String service;
    private final int periodInSeconds;
    private final int timeoutInSeconds;

    public static Builder builder() {
        return new Builder();
    }

    private HealthCheckConfig(final Builder builder) {
        this.targetId = Pattern.compile(builder.targetId);
        this.service = builder.service;
        this.periodInSeconds = builder.periodInSeconds;
        this.timeoutInSeconds = builder.timeoutInSeconds;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String targetId;
        private String service;
        private int periodInSeconds;
        private int timeoutInSeconds;

        @JsonProperty("targetId")
        public Builder targetId(@Nonnull final String targetId) {
            this.targetId = targetId;
            return this;
        }

        @JsonProperty("service")
        public Builder service(@Nonnull final String service) {
            this.service = service;
            return this;
        }

        @JsonProperty("timeout")
        public Builder timeoutInSeconds(final int timeoutInSeconds) {
            this.timeoutInSeconds = timeoutInSeconds;
            return this;
        }

        @JsonProperty("period")
        public Builder periodInSeconds(final int periodInSeconds) {
            this.periodInSeconds = periodInSeconds;
            return this;
        }

        public HealthCheckConfig build() {
            return new HealthCheckConfig(this);
        }
    }
}
