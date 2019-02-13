package io.github.anycollect.micrometer;

import io.micrometer.core.instrument.step.StepRegistryConfig;

public interface Config extends StepRegistryConfig {
    Config DEFAULT = k -> null;

    @Override
    default String prefix() {
        return "anycollect";
    }
}
