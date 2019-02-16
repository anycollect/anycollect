package io.github.anycollect.core.impl.matcher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.regex.Pattern;

@Immutable
public class RegExpMatchRule implements MatchRule {
    private final Pattern instanceIdPattern;
    private final Pattern queryIdPattern;
    private final int periodInSeconds;

    @JsonCreator
    public RegExpMatchRule(
            @JsonProperty(value = "instanceId") @Nullable final String instanceIdPattern,
            @JsonProperty(value = "queryId", required = true) @Nonnull final String queryIdPattern,
            @JsonProperty(value = "period", required = true) final int periodInSeconds) {
        this.instanceIdPattern = instanceIdPattern != null ? Pattern.compile(instanceIdPattern) : Pattern.compile(".*");
        this.queryIdPattern = Pattern.compile(queryIdPattern);
        this.periodInSeconds = periodInSeconds;
    }

    @Override
    public boolean match(@Nonnull final Target target, @Nonnull final Query query) {
        return instanceIdPattern.matcher(target.getId()).matches()
                && queryIdPattern.matcher(query.getId()).matches();
    }

    @Override
    public int getPeriod() {
        return periodInSeconds;
    }
}
