package io.github.anycollect.assertj;

import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;
import org.assertj.core.api.AbstractAssert;

public final class MetricAssert extends AbstractAssert<MetricAssert, Metric> {
    public MetricAssert(final Metric actual) {
        super(actual, MetricAssert.class);
    }

    public static MetricAssert assertThat(final Metric actual) {
        return new MetricAssert(actual);
    }

    public MetricAssert hasKey(final String key) {
        if (!key.equals(actual.getKey())) {
            failWithMessage("expected <%s> to have key %s but was <%s>", actual, key, actual.getKey());
        }
        return this;
    }

    public MetricAssert hasTags(final String... tags) {
        TagsAssert.assertThat(actual.getTags()).hasTags(tags);
        return this;
    }

    public MetricAssert hasMeta(final String... tags) {
        TagsAssert.assertThat(actual.getMeta()).hasTags(tags);
        return this;
    }

    public MetricAssert hasValue(final double value) {
        return hasMeasurement(Stat.VALUE, Type.GAUGE, "", value);
    }

    public MetricAssert hasMeasurement(final Stat stat, final Type type, final String unit, final double value) {
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
