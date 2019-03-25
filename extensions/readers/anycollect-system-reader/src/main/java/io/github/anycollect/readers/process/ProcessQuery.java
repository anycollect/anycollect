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
    private final String prefix;
    private final String cpuUsageKey;
    private final String memUsageKey;
    private final Clock clock;
    private final long totalMemory;

    public ProcessQuery(@Nonnull final String prefix,
                        @Nonnull final String cpuUsageKey,
                        @Nonnull final String memUsageKey,
                        @Nonnull final GlobalMemory memory) {
        super("processes");
        this.prefix = prefix;
        this.cpuUsageKey = cpuUsageKey;
        this.memUsageKey = memUsageKey;
        this.totalMemory = memory.getTotal();
        this.clock = Clock.getDefault();
    }

    public List<Metric> execute(@Nullable final OSProcess previous, @Nonnull final OSProcess current) {
        List<Metric> metrics = new ArrayList<>();
        long rss = current.getResidentSetSize();
        double memoryUsage = 100.0 * rss / totalMemory;
        metrics.add(Metric.builder()
                .key(prefix, memUsageKey)
                .at(clock.wallTime())
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
                .key(prefix, cpuUsageKey)
                .at(clock.wallTime())
                .gauge("percents", cpuUsage)
                .build());
        return metrics;
    }
}
