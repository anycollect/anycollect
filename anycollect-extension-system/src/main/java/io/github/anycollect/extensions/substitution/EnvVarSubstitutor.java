package io.github.anycollect.extensions.substitution;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class EnvVarSubstitutor implements VarSubstitutor {
    @Nullable
    @Override
    public String substitute(@Nonnull final String varName) {
        return System.getenv(varName);
    }
}
