package io.github.anycollect.readers.process.discovery.current;

import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.readers.process.LiveProcess;
import io.github.anycollect.readers.process.Process;
import io.github.anycollect.readers.process.discovery.ProcessDiscovery;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

@Extension(name = CurrentProcessDiscovery.NAME, point = ProcessDiscovery.class)
public final class CurrentProcessDiscovery extends ProcessDiscovery {
    public static final String NAME = "CurrentProcessDiscovery";
    private final Process process;

    @ExtCreator
    public CurrentProcessDiscovery(@ExtConfig @Nonnull final CurrentProcessDiscoveryConfig config) {
        OperatingSystem os = new SystemInfo().getOperatingSystem();
        int pid = os.getProcessId();
        this.process = new LiveProcess(config.targetId(), pid, config.tags(), createMeta(os.getProcess(pid)));
    }

    @Override
    public Set<Process> discover() {
        return Collections.singleton(process);
    }
}
