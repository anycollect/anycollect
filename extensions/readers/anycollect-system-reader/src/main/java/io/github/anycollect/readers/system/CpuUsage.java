package io.github.anycollect.readers.system;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.query.SelfQuery;
import io.github.anycollect.core.api.target.SelfTarget;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;
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
    public List<Metric> executeOn(@Nonnull final SelfTarget target) {
        List<Metric> metrics = new ArrayList<>();
        if (config.totalCpu()) {
            metrics.addAll(computeUsageTotals(target));
        }
        if (config.perCore()) {
            metrics.addAll(computeUsagePerCore(target));
        }
        return metrics;
    }

    private List<Metric> computeUsageTotals(final SelfTarget target) {
        long[] ticks = processor.getSystemCpuLoadTicks();
        if (prevTicks == null) {
            this.prevTicks = ticks;
            return Collections.emptyList();
        }
        long timestamp = clock.wallTime();
        long totalDelta = computeTotal(ticks) - computeTotal(prevTicks);
        List<Metric> metrics = new ArrayList<>();
        if (config.totalCpu()) {
            for (CentralProcessor.TickType tickType : CentralProcessor.TickType.values()) {
                long delta = ticks[tickType.getIndex()] - prevTicks[tickType.getIndex()];
                double percentage = 100.0 * delta / totalDelta;
                metrics.add(makeCpuUsageMetric(
                        tickType.name().toLowerCase(), "total", timestamp, percentage, target));
            }
        }
        if (config.reportActive()) {
            long activeDelta = computeActive(ticks) - computeActive(prevTicks);
            double activePercentage = 100.0 * activeDelta / totalDelta;
            metrics.add(makeCpuUsageMetric(
                    "active", "total", timestamp, activePercentage, target));
        }
        return metrics;
    }

    private List<Metric> computeUsagePerCore(final SelfTarget target) {
        long[][] ticksPerCore = processor.getProcessorCpuLoadTicks();
        if (prevTicksPerCore == null) {
            prevTicksPerCore = ticksPerCore;
            return Collections.emptyList();
        }
        long timestamp = clock.wallTime();
        List<Metric> metrics = new ArrayList<>();
        for (int cpu = 0; cpu < processor.getLogicalProcessorCount(); ++cpu) {
            long totalDelta = computeTotal(ticksPerCore[cpu]) - computeTotal(prevTicksPerCore[cpu]);
            if (config.totalCpu()) {
                for (CentralProcessor.TickType tickType : CentralProcessor.TickType.values()) {
                    long delta = ticksPerCore[cpu][tickType.getIndex()] - prevTicksPerCore[cpu][tickType.getIndex()];
                    double percentage = 100.0 * delta / totalDelta;
                    Metric usage = makeCpuUsageMetric(
                            tickType.name().toLowerCase(), Integer.toString(cpu), timestamp, percentage, target);
                    metrics.add(usage);
                }
            }
            if (config.reportActive()) {
                long activeDelta = computeActive(ticksPerCore[cpu]) - computeActive(prevTicksPerCore[cpu]);
                double activePercentage = 100.0 * activeDelta / totalDelta;
                Metric usage = makeCpuUsageMetric(
                        "active", Integer.toString(cpu), timestamp, activePercentage, target);
                metrics.add(usage);
            }
        }
        return metrics;
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

    private Metric makeCpuUsageMetric(final String state, final String cpu, final long timestamp, final double value,
                                      final SelfTarget target) {
        return Metric.builder()
                .key("cpu.usage")
                .at(timestamp)
                .concatTags(target.getTags())
                .tag("state", state)
                .tag("cpu", cpu)
                .concatMeta(target.getMeta())
                .measurement(Stat.VALUE, Type.GAUGE, "percents", value)
                .build();
    }
}
