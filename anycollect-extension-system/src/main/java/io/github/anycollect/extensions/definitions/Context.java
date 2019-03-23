package io.github.anycollect.extensions.definitions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public interface Context {
    Context EMPTY = new Context() {
        @Override
        public boolean hasInstance(@Nonnull final String name, @Nonnull final String scopeId) {
            return false;
        }

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

        @Override
        public boolean hasDefinition(@Nonnull final String name) {
            return false;
        }

        @Nullable
        @Override
        public Definition getDefinition(@Nonnull final String name) {
            return null;
        }

        @Override
        public List<Instance> getInstances() {
            return Collections.emptyList();
        }
    };

    boolean hasInstance(@Nonnull String name, @Nonnull String scopeId);

    @Nullable
    Instance getInstance(@Nonnull Class<?> type, @Nonnull String scopeId);

    @Nullable
    Instance getInstance(@Nonnull String name, @Nonnull String scopeId);

    boolean hasDefinition(@Nonnull String name);

    @Nullable
    Definition getDefinition(@Nonnull String name);

    List<Instance> getInstances();
}
