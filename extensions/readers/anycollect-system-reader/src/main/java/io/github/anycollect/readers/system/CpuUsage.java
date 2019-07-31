package io.github.anycollect.readers.system;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.query.SelfQuery;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Sample;
import io.github.anycollect.metric.Tags;
import oshi.hardware.CentralProcessor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CpuUsage extends SelfQuery {
    private final CentralProcessor processor;
    private final CpuConfig config;
    private final Clock clock;
    private volatile long[] prevTicks;
    private volatile long[][] prevTicksPerCore;

    public CpuUsage(@Nonnull final CentralProcessor processor,
                    @Nonnull final CpuConfig config) {
        super("cpu.usage");
        this.processor = processor;
        this.config = config;
        this.clock = Clock.getDefault();
    }

    @Override
    public List<Sample> execute() {
        List<Sample> samples = new ArrayList<>();
        if (config.totalCpu()) {
            samples.addAll(computeUsageTotals());
        }
        if (config.perCore()) {
            samples.addAll(computeUsagePerCore());
        }
        return samples;
    }

    private List<Sample> computeUsageTotals() {
        long[] ticks = processor.getSystemCpuLoadTicks();
        if (prevTicks == null) {
            this.prevTicks = ticks;
            return Collections.emptyList();
        }
        long timestamp = clock.wallTime();
        long totalDelta = computeTotal(ticks) - computeTotal(prevTicks);
        List<Sample> samples = new ArrayList<>();
        if (config.totalCpu()) {
            for (CentralProcessor.TickType tickType : CentralProcessor.TickType.values()) {
                long delta = ticks[tickType.getIndex()] - prevTicks[tickType.getIndex()];
                double percentage = 100.0 * delta / totalDelta;
                samples.add(makeCpuUsageMetric(
                        tickType.name().toLowerCase(), "total", timestamp, percentage));
            }
        }
        if (config.reportActive()) {
            long activeDelta = computeActive(ticks) - computeActive(prevTicks);
            double activePercentage = 100.0 * activeDelta / totalDelta;
            samples.add(makeCpuUsageMetric(
                    "active", "total", timestamp, activePercentage));
        }
        prevTicks = ticks;
        return samples;
    }

    private List<Sample> computeUsagePerCore() {
        long[][] ticksPerCore = processor.getProcessorCpuLoadTicks();
        if (prevTicksPerCore == null) {
            prevTicksPerCore = ticksPerCore;
            return Collections.emptyList();
        }
        long timestamp = clock.wallTime();
        List<Sample> samples = new ArrayList<>();
        for (int cpu = 0; cpu < processor.getLogicalProcessorCount(); ++cpu) {
            long totalDelta = computeTotal(ticksPerCore[cpu]) - computeTotal(prevTicksPerCore[cpu]);
            if (config.totalCpu()) {
                for (CentralProcessor.TickType tickType : CentralProcessor.TickType.values()) {
                    long delta = ticksPerCore[cpu][tickType.getIndex()] - prevTicksPerCore[cpu][tickType.getIndex()];
                    double percentage = 100.0 * delta / totalDelta;
                    Sample usage = makeCpuUsageMetric(
                            tickType.name().toLowerCase(), Integer.toString(cpu), timestamp, percentage);
                    samples.add(usage);
                }
            }
            if (config.reportActive()) {
                long activeDelta = computeActive(ticksPerCore[cpu]) - computeActive(prevTicksPerCore[cpu]);
                double activePercentage = 100.0 * activeDelta / totalDelta;
                Sample usage = makeCpuUsageMetric(
                        "active", Integer.toString(cpu), timestamp, activePercentage);
                samples.add(usage);
            }
        }
        prevTicksPerCore = ticksPerCore;
        return samples;
    }

    private long computeTotal(final long[] ticks) {
        long total = 0L;
        for (long tick : ticks) {
            total += tick;
        }
        return total;
    }

    private long computeActive(final long[] ticks) {
        return computeTotal(ticks) - ticks[CentralProcessor.TickType.IDLE.getIndex()];
    }

    private Sample makeCpuUsageMetric(final String state, final String cpu, final long timestamp, final double value) {
        return Metric.builder()
                .key("cpu/usage")
                .tags(Tags.of("state", state, "cpu", cpu))
                .gauge("percents")
                .sample(value, timestamp);
    }
}
