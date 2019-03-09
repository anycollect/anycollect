package io.github.anycollect.extensions.definitions;

import javax.annotation.Nonnull;

public enum Priority {
    DEFAULT(0), OVERRIDE(1);
    private final int priority;

    Priority(final int priority) {
        this.priority = priority;
    }

    boolean isHigherThan(@Nonnull final Priority that) {
        return this.priority > that.priority;
    }
}
