package io.github.anycollect.assertj;

import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.MetricFamily;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;
import org.assertj.core.api.AbstractAssert;

public class MetricFamilyAssert extends AbstractAssert<MetricFamilyAssert, MetricFamily> {
    public MetricFamilyAssert(final MetricFamily actual) {
        super(actual, MetricFamilyAssert.class);
    }

    public static MetricFamilyAssert assertThat(final MetricFamily actual) {
        return new MetricFamilyAssert(actual);
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
