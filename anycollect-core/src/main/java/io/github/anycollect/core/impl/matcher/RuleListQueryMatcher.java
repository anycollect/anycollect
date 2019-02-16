package io.github.anycollect.core.impl.matcher;

import io.github.anycollect.core.api.internal.QueryMatcher;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;

@Immutable
public class RuleListQueryMatcher implements QueryMatcher {
    private final List<MatchRule> rules;

    public RuleListQueryMatcher(@Nonnull final List<MatchRule> rules) {
        this.rules = new ArrayList<>(rules);
    }

    @Override
    public int getPeriodInSeconds(@Nonnull final Target target, @Nonnull final Query query, final int defaultPeriod) {
        int minPeriod = -1;
        for (MatchRule rule : rules) {
            if (rule.match(target, query)) {
                if (minPeriod == -1) {
                    minPeriod = rule.getPeriod();
                } else if (rule.getPeriod() != -1) {
                    minPeriod = Math.min(minPeriod, rule.getPeriod());
                }
            }
        }
        if (minPeriod == -1) {
            return defaultPeriod;
        }
        return minPeriod;
    }
}
