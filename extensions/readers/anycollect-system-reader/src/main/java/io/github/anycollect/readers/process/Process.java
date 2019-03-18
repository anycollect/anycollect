package io.github.anycollect.readers.process;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.target.AbstractTarget;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.metric.Type;
import oshi.software.os.OSProcess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class Process extends AbstractTarget<ProcessStats> {
    private final Clock clock;
    private final int pid;
    private volatile OSProcess previous;

    @JsonCreator
    public Process(@JsonProperty(value = "pid", required = true) final int pid,
                   @JsonProperty("tags") @Nullable final Tags tags,
                   @JsonProperty("meta") @Nullable final Tags meta) {
        super("pid@" + pid, tags != null ? tags : Tags.empty(), meta != null ? meta : Tags.empty());
        this.pid = pid;
        this.clock = Clock.getDefault();
    }

    @Override
    public List<Metric> execute(@Nonnull final ProcessStats query) {
        List<Metric> metrics = new ArrayList<>();
        OSProcess current = query.getOsProcess(pid);
        long totalMemory = query.getTotalMemory();
        long rss = current.getResidentSetSize();
        double memoryUsage = 100.0 * rss / totalMemory;
        metrics.add(Metric.builder()
                .key("process.memory.usage")
                .at(clock.wallTime())
                .concatTags(getTags())
                .concatMeta(getMeta())
                .measurement(Stat.VALUE, Type.GAUGE, "percents", memoryUsage)
                .build());
        if (previous == null) {
            this.previous = copy(current);
        } else {
            long userTimeDelta = current.getUserTime() - previous.getUserTime();
            long kernelTimeDelta = current.getKernelTime() - previous.getKernelTime();
            long upTimeDelta = current.getUpTime() - previous.getUpTime();
            double cpuUsage = 100.0 * (userTimeDelta + kernelTimeDelta) / upTimeDelta;
            metrics.add(Metric.builder()
                    .key("process.cpu.usage")
                    .at(clock.wallTime())
                    .concatTags(getTags())
                    .concatMeta(getMeta())
                    .measurement(Stat.VALUE, Type.GAUGE, "percents", cpuUsage)
                    .build());
        }
        this.previous = copy(current);
        return metrics;
    }

    // we need to make a copy of important fields because oshi can modify this object
    private OSProcess copy(final OSProcess source) {
        OSProcess copy = new OSProcess();
        copy.setUserTime(source.getUserTime());
        copy.setKernelTime(source.getKernelTime());
        copy.setUpTime(source.getUpTime());
        return copy;
    }
}
