package io.github.anycollect.extensions.definitions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public abstract class AbstractDependencyDefinition {
    private final String name;
    private final Class<?> parameterType;
    private final boolean optional;
    private final int position;
    private final boolean single;

    AbstractDependencyDefinition(final String name,
                                 final Class<?> parameterType,
                                 final boolean optional,
                                 final int position,
                                 final boolean single) {
        this.name = name;
        this.parameterType = parameterType;
        this.optional = optional;
        this.position = position;
        this.single = single;
    }
}
