package io.github.anycollect.readers.process;

import io.github.anycollect.core.api.target.AbstractTarget;
import io.github.anycollect.metric.Tags;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class LiveProcess extends AbstractTarget implements Process {
    private final int pid;
    private final OperatingSystem os;

    public LiveProcess(@Nonnull final OperatingSystem os,
                       @Nullable final String targetId,
                       final int pid,
                       @Nullable final Tags tags,
                       @Nullable final Tags meta) {
        super(targetId != null ? targetId : "pid@" + pid,
                tags != null ? tags : Tags.empty(),
                buildMeta(os.getProcess(pid)).concat(meta != null ? meta : Tags.empty()));
        this.pid = pid;
        this.os = os;
    }

    @Override
    public OSProcess snapshot() {
        return os.getProcess(pid);
    }

    private static Tags buildMeta(final OSProcess process) {
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
