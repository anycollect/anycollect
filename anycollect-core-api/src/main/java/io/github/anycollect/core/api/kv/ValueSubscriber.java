package io.github.anycollect.core.api.kv;

import javax.annotation.Nonnull;

public interface ValueSubscriber<T> {
    void onValueChange(@Nonnull T value);
}
