package io.github.anycollect.core.api.query;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.common.Plugin;

import javax.annotation.Nonnull;
import java.util.Set;

public interface QueryProvider<Q extends Query> extends Plugin, Lifecycle {
    @Nonnull
    Set<Q> provide();
}
