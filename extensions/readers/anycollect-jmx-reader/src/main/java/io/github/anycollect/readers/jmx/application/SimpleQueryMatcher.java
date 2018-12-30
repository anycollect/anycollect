package io.github.anycollect.readers.jmx.application;

import io.github.anycollect.readers.jmx.query.Query;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import java.util.Objects;

@EqualsAndHashCode
public final class SimpleQueryMatcher implements QueryMatcher {
    @Nonnull
    private final String targetGroup;
    @Nonnull
    private final String targetLabel;

    public SimpleQueryMatcher(@Nonnull final String targetGroup, @Nonnull final String targetLabel) {
        Objects.requireNonNull(targetGroup, "group must not be null");
        Objects.requireNonNull(targetLabel, "label must not be null");
        this.targetGroup = targetGroup;
        this.targetLabel = targetLabel;
    }

    @Override
    public boolean matches(@Nonnull final Query query) {
        Objects.requireNonNull(query, "query to match must not be null");
        return targetGroup.equals(query.getGroup())
                && targetLabel.equals(query.getLabel());
    }
}
