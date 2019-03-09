package io.github.anycollect.extensions.definitions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Context {
    Context EMPTY = new Context() {
        @Nullable
        @Override
        public Instance getInstance(@Nonnull final Class<?> type, @Nonnull final String scopeId) {
            return null;
        }

        @Nullable
        @Override
        public Instance getInstance(@Nonnull final String name, @Nonnull final String scopeId) {
            return null;
        }
    };

    @Nullable
    Instance getInstance(@Nonnull Class<?> type, @Nonnull String scopeId);

    @Nullable
    Instance getInstance(@Nonnull String name, @Nonnull String scopeId);
}
