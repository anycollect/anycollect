package io.github.anycollect.readers.system;

import org.immutables.value.Value;

@Value.Immutable
public interface CpuConfig {
    CpuConfig DEFAULT = new CpuConfig() { };

    @Value.Default
    default int period() {
        return 5;
    }

    @Value.Default
    default boolean reportActive() {
        return true;
    }

    @Value.Default
    default boolean perCore() {
        return true;
    }

    @Value.Default
    default boolean totalCpu() {
        return true;
    }
}
