package io.github.anycollect.metric;

import io.github.anycollect.metric.frame.ImmutableMetricFrame;
import io.github.anycollect.metric.frame.MetricFrame;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Getter
public final class ImmutableMetric implements Metric {
    private final MetricFrame frame;
    private final long timestamp;
    private final List<Measurement> measurements;

    public ImmutableMetric(@Nonnull final String key,
                           final long timestamp,
                           @Nonnull final List<? extends Measurement> measurements,
                           @Nonnull final Tags tags,
                           @Nonnull final Tags meta) {
        this.frame = new ImmutableMetricFrame(key, tags, meta);
        this.timestamp = timestamp;
        this.measurements = Collections.unmodifiableList(new ArrayList<>(measurements));
    }

    @Override
    public String toString() {
        return frame.getKey() + ";" + (!frame.getTags().isEmpty() ? frame.getTags() + ";" : "") + measurements.stream()
                .map(Measurement::toString)
                .collect(joining(","));
    }

    @Nonnull
    @Override
    public MetricFrame getFrame() {
        return frame;
    }

    @Override
    public int size() {
        return measurements.size();
    }
}
