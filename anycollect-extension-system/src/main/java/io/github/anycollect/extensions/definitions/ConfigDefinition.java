package io.github.anycollect.extensions.definitions;

public class ConfigDefinition extends AbstractDependencyDefinition {
    public ConfigDefinition(final Class<?> configType, final boolean optional, final int position) {
        super("__config__", configType, optional, position, true);
    }
}
