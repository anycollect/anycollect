package io.github.anycollect.metric;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The actual time series data.
 */
@API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
public interface Sample {
    /**
     * Time series of sample.
     *
     * @return time series
     */
    @Nonnull
    Metric getMetric();

    /**
     * Value of sample.
     *
     * @return value
     */
    double getValue();

    /**
     * Timestamp of sample in milliseconds.
     *
     * @return timestamp
     */
    long getTimestamp();

    @Nonnull
    default Key getKey() {
        return getMetric().getKey();
    }

    @Nonnull
    default Tags getTags() {
        return getMetric().getTags();
    }

    @Nonnull
    default Tags getMeta() {
        return getMetric().getMeta();
    }

    @Nonnull
    default Stat getStat() {
        return getMetric().getStat();
    }

    @Nonnull
    default String getUnit() {
        return getMetric().getUnit();
    }

    @Nonnull
    static String toString(@Nullable Sample sample) {
        if (sample == null) {
            return "null";
        }
        return sample.getMetric().toString() + " " + sample.getValue() + " " + sample.getTimestamp();
    }
}
