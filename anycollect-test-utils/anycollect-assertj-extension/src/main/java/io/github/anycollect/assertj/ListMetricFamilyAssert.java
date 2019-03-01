package io.github.anycollect.assertj;

import io.github.anycollect.metric.MetricFamily;
import io.github.anycollect.metric.Tags;
import org.assertj.core.api.AbstractAssert;

import java.util.List;

public class ListMetricFamilyAssert extends AbstractAssert<ListMetricFamilyAssert, List<MetricFamily>> {
    public ListMetricFamilyAssert(final List<MetricFamily> actual) {
        super(actual, ListMetricFamilyAssert.class);
    }

    public MetricFamilyAssert contains(final String key) {
        return contains(key, Tags.empty());
    }

    public MetricFamilyAssert contains(final String key, final Tags tags) {
        for (MetricFamily family : actual) {
            if (key.equals(family.getKey()) && tags.equals(family.getTags())) {
                return new MetricFamilyAssert(family);
            }
        }
        return new MetricFamilyAssert(null);
    }
}