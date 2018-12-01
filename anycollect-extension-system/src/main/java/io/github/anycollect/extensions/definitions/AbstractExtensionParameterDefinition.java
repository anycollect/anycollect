package io.github.anycollect.extensions.definitions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public abstract class AbstractExtensionParameterDefinition {
    private final Class<?> parameterType;
    private final boolean optional;
    private final int position;
    private final boolean single;

    AbstractExtensionParameterDefinition(final Class<?> parameterType,
                                         final boolean optional,
                                         final int position,
                                         final boolean single) {
        this.parameterType = parameterType;
        this.optional = optional;
        this.position = position;
        this.single = single;
    }
}
