package io.github.anycollect.assertj;

import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;
import org.assertj.core.api.AbstractAssert;

public final class MetricFamilyAssert extends AbstractAssert<MetricFamilyAssert, Metric> {
    public MetricFamilyAssert(final Metric actual) {
        super(actual, MetricFamilyAssert.class);
    }

    public static MetricFamilyAssert assertThat(final Metric actual) {
        return new MetricFamilyAssert(actual);
    }

    public MetricFamilyAssert hasKey(final String key) {
        if (!key.equals(actual.getKey())) {
            failWithMessage("expected <%s> to have key %s but was <%s>", actual, key, actual.getKey());
        }
        return this;
    }

    public MetricFamilyAssert hasTags(final String... tags) {
        TagsAssert.assertThat(actual.getTags()).hasTags(tags);
        return this;
    }

    public MetricFamilyAssert hasMeta(final String... tags) {
        TagsAssert.assertThat(actual.getMeta()).hasTags(tags);
        return this;
    }

    public MetricFamilyAssert hasMeasurement(final Stat stat, final Type type, final String unit, final double value) {
        boolean has = false;
        for (Measurement measurement : actual.getMeasurements()) {
            if (stat.equals(measurement.getStat())
                    && type.equals(measurement.getType())
                    && unit.equals(measurement.getUnit())
                    && value == measurement.getValue()) {
                has = true;
                break;
            }
        }
        if (!has) {
            failWithMessage("Expected <%s> to have measurement of stat <%s>, type <%s>, unit <%s> and value <%s>",
                    actual, stat, type, unit, value);
        }
        return this;
    }
}
