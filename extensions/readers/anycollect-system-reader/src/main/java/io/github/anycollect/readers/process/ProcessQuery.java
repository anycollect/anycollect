package io.github.anycollect.readers.process;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.query.AbstractQuery;
import io.github.anycollect.metric.Metric;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSProcess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class ProcessQuery extends AbstractQuery {
    private final Clock clock;
    private final GlobalMemory memory;

    public ProcessQuery(@Nonnull final GlobalMemory memory) {
        super("processes");
        this.memory = memory;
        this.clock = Clock.getDefault();
    }

    public List<Metric> execute(@Nullable final OSProcess previous, @Nonnull final OSProcess current) {
        List<Metric> metrics = new ArrayList<>();
        long totalMemory = memory.getTotal();
        long rss = current.getResidentSetSize();
        double memoryUsage = 100.0 * rss / totalMemory;
        metrics.add(Metric.builder()
                .key("process.memory.usage")
                .at(clock.wallTime())
                .concatTags(getTags())
                .concatMeta(getMeta())
                .gauge("percents", memoryUsage)
                .build());
        if (previous == null) {
            return metrics;
        }
        long userTimeDelta = current.getUserTime() - previous.getUserTime();
        long kernelTimeDelta = current.getKernelTime() - previous.getKernelTime();
        long upTimeDelta = current.getUpTime() - previous.getUpTime();
        double cpuUsage = 100.0 * (userTimeDelta + kernelTimeDelta) / upTimeDelta;
        metrics.add(Metric.builder()
                .key("process.cpu.usage")
                .at(clock.wallTime())
                .concatTags(getTags())
                .concatMeta(getMeta())
                .gauge("percents", cpuUsage)
                .build());
        return metrics;
    }
}
