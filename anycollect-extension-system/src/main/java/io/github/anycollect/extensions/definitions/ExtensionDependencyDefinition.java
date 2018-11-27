package io.github.anycollect.extensions.definitions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class ExtensionDependencyDefinition {
    private final String name;
    private final Class<?> dependencyType;
    private final boolean optional;
    private final int constructorParameterNumber;

    public ExtensionDependencyDefinition(final String name,
                                         final Class<?> dependencyType,
                                         final boolean optional,
                                         final int constructorParameterNumber) {
        this.name = name;
        this.dependencyType = dependencyType;
        this.optional = optional;
        this.constructorParameterNumber = constructorParameterNumber;
    }
}
