package io.github.anycollect.extensions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

final class FirstNonNullVarSubstitutor implements VarSubstitutor {
    private final List<VarSubstitutor> substitutors;

    FirstNonNullVarSubstitutor(final VarSubstitutor... substitutors) {
        this.substitutors = Arrays.asList(substitutors);
    }

    @Nullable
    @Override
    public Object substitute(@Nonnull final String varName) {
        for (VarSubstitutor substitutor : substitutors) {
            Object value = substitutor.substitute(varName);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
