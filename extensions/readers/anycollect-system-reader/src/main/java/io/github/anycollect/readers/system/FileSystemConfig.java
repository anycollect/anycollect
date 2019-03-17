package io.github.anycollect.readers.system;

import org.immutables.value.Value;

import java.util.Collections;
import java.util.List;

@Value.Immutable
public interface FileSystemConfig {
    FileSystemConfig DEFAULT = new FileSystemConfig() { };

    @Value.Default
    default int period() {
        return 5;
    }

    @Value.Default
    default List<String> ignoreFileSystems() {
        return Collections.emptyList();
    }

    @Value.Default
    default boolean reportOpenDescriptors() {
        return true;
    }
}
