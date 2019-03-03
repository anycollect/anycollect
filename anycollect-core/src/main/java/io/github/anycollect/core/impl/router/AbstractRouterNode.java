package io.github.anycollect.core.impl.router;

import javax.annotation.Nonnull;

public abstract class AbstractRouterNode implements RouterNode {
    private final String address;

    public AbstractRouterNode(@Nonnull final String address) {
        this.address = address;
    }

    @Nonnull
    @Override
    public final String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return address;
    }
}
