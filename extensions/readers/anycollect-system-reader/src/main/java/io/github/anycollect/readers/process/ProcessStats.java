package io.github.anycollect.readers.process;

import io.github.anycollect.core.api.query.AbstractQuery;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import javax.annotation.Nonnull;

public final class ProcessStats extends AbstractQuery {
    private final OperatingSystem os;
    private final GlobalMemory memory;

    public ProcessStats(@Nonnull final OperatingSystem os, @Nonnull final GlobalMemory memory) {
        super("processes");
        this.os = os;
        this.memory = memory;
    }

    public OSProcess getOsProcess(final int pid) {
        return os.getProcess(pid);
    }

    public long getTotalMemory() {
        return memory.getTotal();
    }
}
