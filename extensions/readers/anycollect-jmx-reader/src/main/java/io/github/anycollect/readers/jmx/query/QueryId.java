package io.github.anycollect.readers.jmx.query;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Getter
@Immutable
@EqualsAndHashCode
public final class QueryId {
    @Nonnull
    private final String group;
    @Nonnull
    private final String label;

    public QueryId(@Nonnull final String group, @Nonnull final String label) {
        Objects.requireNonNull(group, "group must not be null");
        Objects.requireNonNull(label, "label must not be null");
        this.group = group;
        this.label = label;
    }
}
