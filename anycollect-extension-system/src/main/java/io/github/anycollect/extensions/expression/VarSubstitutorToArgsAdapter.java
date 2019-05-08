package io.github.anycollect.extensions.expression;

import io.github.anycollect.extensions.common.expression.Args;
import io.github.anycollect.extensions.substitution.VarSubstitutor;

import javax.annotation.Nonnull;

public final class VarSubstitutorToArgsAdapter implements Args {
    private final VarSubstitutor vars;

    public VarSubstitutorToArgsAdapter(@Nonnull final VarSubstitutor vars) {
        this.vars = vars;
    }

    @Override
    public boolean contains(@Nonnull final String key) {
        return vars.substitute(key) != null;
    }

    @Nonnull
    @Override
    public String get(@Nonnull final String key) {
        String value = vars.substitute(key);
        if (value == null) {
            throw new IllegalArgumentException("there is no values associated with " + key);
        }
        return value;
    }
}
