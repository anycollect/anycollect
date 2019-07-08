package io.github.anycollect.assertj;

import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import org.assertj.core.api.ListAssert;

import java.util.List;

public class MetricsAssert extends ListAssert<Metric> {
    public static MetricsAssert assertThat(final List<Metric> actual) {
        return new MetricsAssert(actual);
    }

    public MetricsAssert(final List<Metric> actual) {
        super(actual);
    }

    public MetricAssert contains(final String key) {
        return contains(key, Tags.empty());
    }

    public MetricAssert contains(final String key, final Tags tags) {
        for (Metric family : actual) {
            if (key.equals(family.getKey()) && tags.equals(family.getTags())) {
                return new MetricAssert(family);
            }
        }
        return new MetricAssert(null);
    }

    public MetricAssert contains(final String key, final Tags tags, final Tags meta) {
        for (Metric metric : actual) {
            if (key.equals(metric.getKey()) && tags.equals(metric.getTags()) && meta.equals(metric.getMeta())) {
                return new MetricAssert(metric);
            }
        }
        return new MetricAssert(null);
    }
}
