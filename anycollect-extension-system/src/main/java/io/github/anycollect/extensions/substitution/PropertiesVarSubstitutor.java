package io.github.anycollect.extensions.substitution;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Properties;

public final class PropertiesVarSubstitutor implements VarSubstitutor {
    private final Properties properties;

    public PropertiesVarSubstitutor(@Nonnull final Properties properties) {
        this.properties = properties;
    }

    @Nullable
    @Override
    public String substitute(@Nonnull final String varName) {
        return properties.getProperty(varName);
    }

    @Override
    public String toString() {
        return properties.toString();
    }
}
