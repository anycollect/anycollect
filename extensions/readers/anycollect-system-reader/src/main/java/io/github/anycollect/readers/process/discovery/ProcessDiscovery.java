package io.github.anycollect.readers.process.discovery;

import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.process.Process;
import oshi.software.os.OSProcess;

import javax.annotation.Nonnull;

public abstract class ProcessDiscovery implements ServiceDiscovery<Process> {
    protected Tags createMeta(@Nonnull final OSProcess process) {
        return Tags.builder()
                .tag("pid", process.getProcessID())
                .tag("user", process.getUser())
                .tag("group", process.getGroup())
                .tag("process.name", process.getName())
                .tag("command.line", process.getCommandLine())
                .tag("target.kind", "process")
                .build();
    }
}
