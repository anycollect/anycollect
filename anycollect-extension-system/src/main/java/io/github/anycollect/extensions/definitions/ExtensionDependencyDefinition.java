package io.github.anycollect.extensions.definitions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class ExtensionDependencyDefinition extends AbstractExtensionParameterDefinition {
    private final String name;

    private ExtensionDependencyDefinition(final String name,
                                          final Class<?> dependencyType,
                                          final boolean optional,
                                          final int position,
                                          final boolean single) {
        super(dependencyType, optional, position, single);
        this.name = name;
    }

    public static ExtensionDependencyDefinition single(final String name,
                                                       final Class<?> dependencyType,
                                                       final boolean optional,
                                                       final int position) {
        return new ExtensionDependencyDefinition(name, dependencyType, optional, position, false);
    }

    public static ExtensionDependencyDefinition multiple(final String name,
                                                         final Class<?> collectionParameterType,
                                                         final boolean optional,
                                                         final int position) {
        return new ExtensionDependencyDefinition(name, collectionParameterType, optional, position, true);
    }
}
