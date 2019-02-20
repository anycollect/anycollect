package io.github.anycollect.core.api.measurable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A wrapped object that provides methods to extract some properties for a given path from an enclosed object.
 *
 * May return null if could not find tag/value/unit for given path.
 */
public interface Measurable {
    @Nullable
    String getTag(@Nonnull String path);

    @Nullable
    Object getValue(@Nonnull String path);

    @Nullable
    String getUnit(@Nonnull String path);
}
