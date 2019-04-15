package io.github.anycollect.extensions.substitution;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public interface VarSubstitutor {
    VarSubstitutor EMPTY = new VarSubstitutor() {
        @Override
        @Nullable
        public Object substitute(@Nonnull final String varName) {
            return null;
        }
    };

    static VarSubstitutor firstNonNull(VarSubstitutor... substitutors) {
        return new FirstNonNullVarSubstitutor(substitutors);
    }

    static VarSubstitutor ofMap(Map<String, String> map) {
        return new MapVarSubstitutor(map);
    }

    static VarSubstitutor env() {
        return new EnvVarSubstitutor();
    }

    @Nullable
    Object substitute(@Nonnull String varName);
}
