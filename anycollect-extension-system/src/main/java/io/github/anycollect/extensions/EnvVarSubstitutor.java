package io.github.anycollect.extensions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class EnvVarSubstitutor implements VarSubstitutor {
    @Nullable
    @Override
    public Object substitute(@Nonnull final String varName) {
        return System.getenv(varName);
    }
}
