package io.github.anycollect.extensions;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.extensions.substitution.VarSubstitutor;

import javax.annotation.Nonnull;

public final class PropertyActivation implements Activation {
    private final VarSubstitutor varSubstitutor;
    private final String propertyName;
    private final String propertyValue;

    @JsonCreator
    public PropertyActivation(@JacksonInject("environment") @Nonnull final VarSubstitutor varSubstitutor,
                              @JsonProperty(value = "name", required = true) @Nonnull final String propertyName,
                              @JsonProperty(value = "value", required = true) @Nonnull final String propertyValue) {
        this.varSubstitutor = varSubstitutor;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    @Override
    public boolean isReached() {
        return propertyValue.equals(varSubstitutor.substitute(propertyName));
    }
}
