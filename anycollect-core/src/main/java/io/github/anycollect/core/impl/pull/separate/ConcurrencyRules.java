package io.github.anycollect.core.impl.pull.separate;

import io.github.anycollect.core.api.target.Target;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@ToString
public final class ConcurrencyRules implements ConcurrencyRule {
    private final List<ConcurrencyRule> rules;

    public static Builder builder() {
        return new Builder();
    }

    private ConcurrencyRules(final Builder builder) {
        this.rules = new ArrayList<>(builder.rules);
    }

    @Override
    public int getPoolSize(@Nonnull final Target target, final int fallback) {
        for (ConcurrencyRule rule : rules) {
            int poolSize = rule.getPoolSize(target, -1);
            if (poolSize != -1) {
                return poolSize;
            }
        }
        return fallback;
    }

    public static final class Builder {
        private final List<ConcurrencyRule> rules = new ArrayList<>();

        public Builder withRule(@Nonnull final ConcurrencyRule rule) {
            this.rules.add(rule);
            return this;
        }

        public Builder withRules(@Nonnull final List<ConcurrencyRule> list) {
            this.rules.addAll(list);
            return this;
        }

        public ConcurrencyRules build() {
            return new ConcurrencyRules(this);
        }
    }
}
