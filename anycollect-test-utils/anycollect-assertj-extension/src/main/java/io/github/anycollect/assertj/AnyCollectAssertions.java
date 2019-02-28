package io.github.anycollect.assertj;

import io.github.anycollect.metric.MetricFamily;
import io.github.anycollect.metric.Tags;
import org.assertj.core.api.Assertions;

import java.util.List;

public final class AnyCollectAssertions extends Assertions {
    private AnyCollectAssertions() {
    }

    public static TagsAssert assertThat(final Tags actual) {
        return TagsAssert.assertThat(actual);
    }

    public static MetricFamilyAssert assertThat(final MetricFamily actual) {
        return MetricFamilyAssert.assertThat(actual);
    }

    public static ListMetricFamilyAssert assertThatFamilies(final List<MetricFamily> actual) {
        return new ListMetricFamilyAssert(actual);
    }
}
