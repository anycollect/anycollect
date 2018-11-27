package io.github.anycollect.extensions.definitions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class ExtensionDependencyDefinition extends AbstractExtensionParameterDefinition {
    private final String name;

    public ExtensionDependencyDefinition(final String name,
                                         final Class<?> dependencyType,
                                         final boolean optional,
                                         final int position) {
        super(dependencyType, optional, position);
        this.name = name;
    }
}
