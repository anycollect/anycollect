package io.github.anycollect.core.impl.pull;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.impl.pull.availability.HealthChecksConfig;
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
    private static final int UPDATE_PERIOD_IN_SECONDS = 60;
    private static final int DEFAULT_PULL_INTERVAL_IN_SECONDS = 30;
    private static final int DEFAULT_POOL_SIZE = 2;

    private final int updatePeriodInSeconds;
    private final int defaultPullPeriodInSeconds;
    private final int defaultPoolSize;
    private final ConcurrencyRule concurrencyRule;
    private final HealthChecksConfig healthChecks;
    private final Clock clock;

    public static Builder builder() {
        return new Builder();
    }

    private PullManagerConfig(final Builder builder) {
        this.updatePeriodInSeconds = builder.updatePeriodInSeconds;
        this.defaultPullPeriodInSeconds = builder.defaultPullPeriodInSeconds;
        this.defaultPoolSize = builder.defaultPoolSize;
        this.concurrencyRule = builder.concurrencyRulesBuilder.build();
        this.healthChecks = builder.healthChecks;
        this.clock = builder.clock;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private int updatePeriodInSeconds = UPDATE_PERIOD_IN_SECONDS;
        private int defaultPullPeriodInSeconds = DEFAULT_PULL_INTERVAL_IN_SECONDS;
        private int defaultPoolSize = DEFAULT_POOL_SIZE;
        private ConcurrencyRules.Builder concurrencyRulesBuilder = ConcurrencyRules.builder();
        private HealthChecksConfig healthChecks;
        private Clock clock;

        @JacksonInject
        public Builder withClock(@Nonnull final Clock clock) {
            this.clock = clock;
            return this;
        }

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

        @JsonProperty("healthChecks")
        public Builder withRules(@Nonnull final HealthChecksConfig healthChecks) {
            this.healthChecks = healthChecks;
            return this;
        }

        public PullManagerConfig build() {
            return new PullManagerConfig(this);
        }
    }
}
