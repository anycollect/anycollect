package io.github.anycollect.extensions.substitution;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

final class MapVarSubstitutor implements VarSubstitutor {
    private final Map<String, String> map;

    MapVarSubstitutor(final Map<String, String> map) {
        this.map = new HashMap<>(map);
    }

    @Nullable
    @Override
    public String substitute(@Nonnull final String varName) {
        return map.get(varName);
    }
}
