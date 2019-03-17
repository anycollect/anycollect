package io.github.anycollect.readers.system;

import org.immutables.value.Value;

@Value.Immutable
public interface SystemConfig {
    SystemConfig DEFAULT = new SystemConfig() { };

    @Value.Default
    default CpuConfig cpu() {
        return CpuConfig.DEFAULT;
    }

    @Value.Default
    default FileSystemConfig fs() {
        return FileSystemConfig.DEFAULT;
    }
}
