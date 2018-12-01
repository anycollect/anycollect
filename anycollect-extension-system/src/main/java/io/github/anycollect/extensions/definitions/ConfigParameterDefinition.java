package io.github.anycollect.extensions.definitions;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class ConfigParameterDefinition extends AbstractExtensionParameterDefinition {
    public ConfigParameterDefinition(final Class<?> configType,
                                     final boolean optional,
                                     final int position) {
        super(configType, optional, position, true);
    }
}
