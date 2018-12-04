package io.github.anycollect.extensions.definitions;

public final class ConfigDefinition extends AbstractDependencyDefinition {
    public static final String CONFIG_PARAMETER_NAME = "__config__";
    private final String configKey;

    public ConfigDefinition(final Class<?> configType,
                            final boolean optional,
                            final int position) {
        super(CONFIG_PARAMETER_NAME, configType, optional, position, true);
        this.configKey = "";
    }

    public ConfigDefinition(final String configKey,
                            final Class<?> configType,
                            final boolean optional,
                            final int position,
                            final boolean single) {
        super(CONFIG_PARAMETER_NAME, configType, optional, position, single);
        this.configKey = configKey;
    }

    public String getConfigKey() {
        return configKey;
    }
}
