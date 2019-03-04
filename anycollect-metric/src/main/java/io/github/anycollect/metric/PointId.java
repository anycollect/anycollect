package io.github.anycollect.metric;

import javax.annotation.Nonnull;

@Deprecated
public interface PointId extends Id {
    static ImmutablePointId.Builder key(@Nonnull String key) {
        return new ImmutablePointId.Builder(key);
    }

    @Nonnull
    String getKey();

    @Nonnull
    String getUnit();

    @Nonnull
    Stat getStat();

    @Nonnull
    Type getType();
}
