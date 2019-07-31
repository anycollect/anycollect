package io.github.anycollect.readers.process;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.job.TaggingJob;
import io.github.anycollect.core.api.query.AbstractQuery;
import io.github.anycollect.metric.Key;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Sample;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSProcess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class ProcessQuery extends AbstractQuery<Process> {
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

    public List<Sample> execute(@Nullable final OSProcess previous, @Nonnull final OSProcess current) {
        List<Sample> samples = new ArrayList<>();
        long rss = current.getResidentSetSize();
        double memoryUsagePercent = 100.0 * rss / totalMemory;
        long timestamp = clock.wallTime();
        samples.add(Metric.builder()
                .key(Key.of(memUsageKey).withPrefix(prefix))
                .gauge("percents")
                .sample(memoryUsagePercent, timestamp));
        samples.add(Metric.builder()
                .key(Key.of(memUsageKey).withPrefix(prefix))
                .gauge("bytes")
                .sample(rss, timestamp));
        if (previous == null) {
            return samples;
        }
        long userTimeDelta = current.getUserTime() - previous.getUserTime();
        long kernelTimeDelta = current.getKernelTime() - previous.getKernelTime();
        long upTimeDelta = current.getUpTime() - previous.getUpTime();
        double cpuUsage = 100.0 * (userTimeDelta + kernelTimeDelta) / upTimeDelta;
        samples.add(Metric.builder()
                .key(Key.of(cpuUsageKey).withPrefix(prefix))
                .gauge("percents")
                .sample(cpuUsage, timestamp));
        return samples;
    }

    @Nonnull
    @Override
    public Job bind(@Nonnull final Process target) {
        return new TaggingJob(null, target, this, new ProcessJob(target, this));
    }
}
