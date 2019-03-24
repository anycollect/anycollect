package io.github.anycollect.extensions.definitions;

import javax.annotation.Nonnull;

public interface ExtendableContext extends Context {
    void addInstance(@Nonnull Instance instance);

    void addDefinition(@Nonnull Definition definition);
}
