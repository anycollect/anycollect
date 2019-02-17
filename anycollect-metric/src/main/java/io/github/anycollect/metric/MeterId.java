package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public interface MeterId extends Id {
    static ImmutableMeterId.Builder key(@Nonnull String key) {
        return new ImmutableMeterId.Builder().key(key);
    }

    @Nonnull
    String getKey();

    @Nonnull
    String getUnit();
}
