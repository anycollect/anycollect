package io.github.anycollect.core.api.measurable;

import javax.annotation.Nonnull;

public interface Measurable {
    String getTag(@Nonnull String path);

    Object getValue(@Nonnull String path);

    String getUnit(@Nonnull String path);
}
