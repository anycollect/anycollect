package io.github.anycollect.readers.process.discovery.current;

import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.process.LiveProcess;
import io.github.anycollect.readers.process.Process;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

@Extension(name = CurrentProcessDiscovery.NAME, point = ServiceDiscovery.class)
public final class CurrentProcessDiscovery implements ServiceDiscovery<Process> {
    public static final String NAME = "CurrentProcessDiscovery";
    private final Process process;

    @ExtCreator
    public CurrentProcessDiscovery(@ExtConfig @Nonnull final CurrentProcessDiscoveryConfig config) {
        OperatingSystem os = new SystemInfo().getOperatingSystem();
        int pid = os.getProcessId();
        this.process = new LiveProcess(new SystemInfo().getOperatingSystem(), config.targetId(), pid, config.tags(), Tags.empty());
    }

    @Override
    public Set<Process> discover() {
        return Collections.singleton(process);
    }
}
