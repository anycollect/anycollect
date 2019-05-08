package io.github.anycollect.extensions.common.expression;

import javax.annotation.Nonnull;

public interface Args {
    boolean contains(@Nonnull String key);

    @Nonnull
    String get(@Nonnull String key);
}
