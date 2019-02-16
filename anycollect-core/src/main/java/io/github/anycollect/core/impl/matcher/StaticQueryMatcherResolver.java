package io.github.anycollect.core.impl.matcher;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.internal.QueryMatcher;
import io.github.anycollect.core.api.internal.QueryMatcherResolver;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.List;

@Immutable
@Extension(name = StaticQueryMatcherResolver.NAME, point = QueryMatcherResolver.class)
public class StaticQueryMatcherResolver implements QueryMatcherResolver {
    public static final String NAME = "StaticQueryMatcherResolver";
    private final RuleListQueryMatcher matcher;

    @ExtCreator
    public StaticQueryMatcherResolver(@ExtConfig @Nonnull final Config config) {
        matcher = new RuleListQueryMatcher(config.rules);
    }

    @Override
    public QueryMatcher current() {
        return matcher;
    }

    public static class Config {
        private final List<MatchRule> rules;

        public Config(@JsonProperty("rules") @Nonnull final List<MatchRule> rules) {
            this.rules = rules;
        }
    }
}
