package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public interface Meter {
    @Nonnull
    MeterId getId();
}
