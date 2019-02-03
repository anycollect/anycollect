package io.github.anycollect.core.api.query;

import javax.annotation.Nonnull;

public interface Query {
    @Nonnull
    String getGroup();

    @Nonnull
    String getLabel();
}
