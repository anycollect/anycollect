package io.github.anycollect.extensions.definitions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractScope implements Scope {
    private final Scope parent;

    public AbstractScope(@Nullable final Scope parent) {
        this.parent = parent;
    }

    @Override
    @Nullable
    public final Scope getParent() {
        return parent;
    }

    @Override
    public final boolean isParent(@Nonnull final Scope that) {
        return distance(that) != -1;
    }

    @Override
    public final int distance(@Nonnull final Scope that) {
        Scope current = that;
        int distance = 0;
        while (current != null) {
            if (current.equals(this)) {
                return distance;
            }
            current = current.getParent();
        }
        return -1;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (parent != null) {
            builder.append(parent).append("->");
        }
        builder.append(getId());
        return builder.toString();
    }
}
