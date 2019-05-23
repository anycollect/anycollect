package io.github.anycollect.core.impl.pull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.impl.pull.separate.ConcurrencyRule;
import io.github.anycollect.core.impl.pull.separate.ConcurrencyRules;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.List;

@Getter
@ToString
@JsonDeserialize(builder = PullManagerConfig.Builder.class)
public final class PullManagerConfig {
    private static final int DEFAULT_UPDATE_PERIOD_IN_SECONDS = 60;
    private static final int DEFAULT_PULL_PERIOD_IN_SECONDS = 30;
    private static final int DEFAULT_HEALTH_CHECK_PERIOD_IN_SECONDS = 10;
    private static final int DEFAULT_POOL_SIZE = 1;

    private final int updatePeriodInSeconds;
    private final int defaultPullPeriodInSeconds;
    private final int defaultPoolSize;
    private final ConcurrencyRule concurrencyRule;
    private final int healthCheckPeriodInSeconds;
    private final Clock clock;

    public static Builder builder() {
        return new Builder();
    }

    private PullManagerConfig(final Builder builder) {
        this.updatePeriodInSeconds = builder.updatePeriodInSeconds;
        this.defaultPullPeriodInSeconds = builder.defaultPullPeriodInSeconds;
        this.defaultPoolSize = builder.defaultPoolSize;
        this.concurrencyRule = builder.concurrencyRulesBuilder.build();
        this.healthCheckPeriodInSeconds = builder.healthCheckPeriodInSeconds;
        this.clock = builder.clock;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private int updatePeriodInSeconds = DEFAULT_UPDATE_PERIOD_IN_SECONDS;
        private int healthCheckPeriodInSeconds = DEFAULT_HEALTH_CHECK_PERIOD_IN_SECONDS;
        private int defaultPullPeriodInSeconds = DEFAULT_PULL_PERIOD_IN_SECONDS;
        private int defaultPoolSize = DEFAULT_POOL_SIZE;
        private ConcurrencyRules.Builder concurrencyRulesBuilder = ConcurrencyRules.builder();
        private Clock clock = Clock.getDefault();

        @JsonProperty("updatePeriod")
        public Builder withUpdatePeriod(final int seconds) {
            this.updatePeriodInSeconds = seconds;
            return this;
        }

        @JsonProperty("pullPeriod")
        public Builder withDefaultPullPeriod(final int seconds) {
            this.defaultPullPeriodInSeconds = seconds;
            return this;
        }

        @JsonProperty("defaultPoolSize")
        public Builder withDefaultPoolSize(final int numberOfThread) {
            this.defaultPoolSize = numberOfThread;
            return this;
        }

        public Builder withRule(@Nonnull final ConcurrencyRule rule) {
            this.concurrencyRulesBuilder.withRule(rule);
            return this;
        }

        @JsonProperty("rules")
        public Builder withRules(@Nonnull final List<ConcurrencyRule> rules) {
            this.concurrencyRulesBuilder.withRules(rules);
            return this;
        }

        @JsonProperty("healthCheckPeriod")
        public Builder withHealthCheckPeriod(final int seconds) {
            this.healthCheckPeriodInSeconds = seconds;
            return this;
        }

        public PullManagerConfig build() {
            return new PullManagerConfig(this);
        }
    }
}
