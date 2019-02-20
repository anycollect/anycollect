package io.github.anycollect.assertj;

import io.github.anycollect.metric.MetricFamily;
import io.github.anycollect.metric.Tags;
import org.assertj.core.api.Assertions;

public final class AnyCollectAssertions extends Assertions {
    private AnyCollectAssertions() {
    }

    public static TagsAssert assertThat(final Tags actual) {
        return TagsAssert.assertThat(actual);
    }

    public static MetricFamilyAssert assertThat(final MetricFamily actual) {
        return MetricFamilyAssert.assertThat(actual);
    }
}
