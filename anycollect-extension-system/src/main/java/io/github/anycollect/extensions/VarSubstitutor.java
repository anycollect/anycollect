package io.github.anycollect.extensions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface VarSubstitutor {
    VarSubstitutor EMPTY = new VarSubstitutor() {
        @Override
        @Nullable
        public Object substitute(@Nonnull final String varName) {
            return null;
        }
    };

    @Nullable
    Object substitute(@Nonnull String varName);
}
