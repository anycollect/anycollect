package io.github.anycollect.core.api.query;

import javax.annotation.Nonnull;

public interface Query {
    @Nonnull
    String group();

    @Nonnull
    String label();
}
