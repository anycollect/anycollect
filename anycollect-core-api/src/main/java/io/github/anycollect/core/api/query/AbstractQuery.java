package io.github.anycollect.core.api.query;

import javax.annotation.Nonnull;

public abstract class AbstractQuery implements Query {
    private final String group;
    private final String label;

    public AbstractQuery(@Nonnull final String group, @Nonnull final String label) {
        this.group = group;
        this.label = label;
    }

    @Nonnull
    @Override
    public final String getGroup() {
        return group;
    }

    @Nonnull
    @Override
    public final String getLabel() {
        return label;
    }
}
