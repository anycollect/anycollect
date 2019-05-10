package io.github.anycollect.writers.collectd;

import io.github.anycollect.extensions.common.expression.Args;
import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;

public final class MetricArgs implements Args {
    private final Metric metric;
    private final Measurement measurement;

    public MetricArgs(@Nonnull final Metric metric, @Nonnull final Measurement measurement) {
        this.metric = metric;
        this.measurement = measurement;
    }

    @Override
    public boolean contains(@Nonnull final String key) {
        return "what".equals(key)
                || "stat".equals(key)
                || "mtype".equals(key)
                || metric.getTags().hasTagKey(key);
    }

    @Nonnull
    @Override
    public String get(@Nonnull final String key) {
        if ("what".equals(key)) {
            return metric.getKey();
        }
        if ("stat".equals(key)) {
            return measurement.getStat().getTagValue();
        }
        if ("mtype".equals(key)) {
            return measurement.getType().getTagValue();
        }
        return metric.getTags().getTag(key).getValue();
    }
}
