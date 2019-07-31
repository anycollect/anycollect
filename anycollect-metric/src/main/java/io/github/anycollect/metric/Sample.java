package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public interface Sample {
    @Nonnull
    Metric getMetric();

    double getValue();

    long getTimestamp();

    default Key getKey() {
        return getMetric().getKey();
    }

    default Tags getTags() {
        return getMetric().getTags();
    }

    default Tags getMeta() {
        return getMetric().getMeta();
    }

    default Stat getStat() {
        return getMetric().getStat();
    }

    default Type getType() {
        return getMetric().getType();
    }

    default String getUnit() {
        return getMetric().getUnit();
    }

    static String toString(Sample sample) {
        return sample.getMetric().toString() + " " + sample.getValue() + " " + sample.getTimestamp();
    }
}
