package io.github.anycollect.readers.process;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.job.TaggingJob;
import io.github.anycollect.core.api.target.AbstractTarget;
import io.github.anycollect.metric.Tags;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Process extends AbstractTarget<ProcessQuery> {
    private final int pid;
    private final OperatingSystem os;

    @JsonCreator
    public Process(@JsonProperty("id") @Nullable final String targetId,
                   @JsonProperty(value = "pid", required = true) final int pid,
                   @JsonProperty("tags") @Nullable final Tags tags,
                   @JsonProperty("meta") @Nullable final Tags meta) {
        super(targetId != null ? targetId : "pid@" + pid,
                tags != null ? tags : Tags.empty(),
                meta != null ? meta : Tags.empty());
        this.pid = pid;
        this.os = new SystemInfo().getOperatingSystem();
    }

    public OSProcess snapshot() {
        return os.getProcess(pid);
    }

    @Nonnull
    @Override
    public Job bind(@Nonnull final ProcessQuery query) {
        return new TaggingJob("", getTags(), getMeta(), new ProcessJob(this, query));
    }
}
