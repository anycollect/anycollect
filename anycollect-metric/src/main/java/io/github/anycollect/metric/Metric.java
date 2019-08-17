package io.github.anycollect.metric;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * Metric represents a time series.
 * <p>
 * All implementation must be immutable and can be safely shared between threads.
 */
@Immutable
@API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
public interface Metric {
    @Nonnull
    static KeyStageBuilder builder() {
        return new MetricBuilder();
    }

    /**
     * Specifies the main property of the system that is measured.
     *
     * @return the key of time series
     * @see Key
     */
    @Nonnull
    Key getKey();

    /**
     * Specifies the key-value pairs related to the time series.
     * <p>
     * E.g. http.method=POST
     *
     * @return the tags of the time series
     */
    @Nonnull
    Tags getTags();

    /**
     * Specifies the meta information that is related to the time series.
     * <p>
     * This is not a part of metric identity but it can help to perform some filtering and transformation actions
     * to integrate with different external models of time series.
     *
     * @return meta information of the time series
     */
    @Nonnull
    Tags getMeta();

    @Nonnull
    Stat getStat();

    @Nonnull
    Type getType();

    @Nonnull
    String getUnit();

    @Nonnull
    default MutableMetric modify() {
        return new MutableMetricImpl(this);
    }

    @Nonnull
    default Sample sample(double value, long timestamp) {
        return new DoubleSample(this, value, timestamp);
    }

    /**
     * Computes hash code based on general contract.
     *
     * @param metric - the instance whose hash code to compute
     * @return hash code
     */
    static int hash(@Nonnull Metric metric) {
        return Objects.hash(metric.getKey(), metric.getTags(), metric.getStat(), metric.getType(), metric.getUnit());
    }

    /**
     * @param first  - first metric to be compared
     * @param second - second metric to be compared
     * @return {@code true} if first is equal to second argument
     */
    static boolean equals(@Nullable Metric first, @Nullable Metric second) {
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        if (!first.getStat().equals(second.getStat())) {
            return false;
        }
        if (!first.getType().equals(second.getType())) {
            return false;
        }
        if (!first.getUnit().equals(second.getUnit())) {
            return false;
        }
        if (!first.getKey().equals(second.getKey())) {
            return false;
        }
        if (!first.getTags().equals(second.getTags())) {
            return false;
        }
        return true;
    }

    @Nonnull
    static String toString(@Nullable Metric metric) {
        if (metric == null) {
            return "null";
        }
        return metric.getKey() + ";"
                + (!metric.getTags().isEmpty() ? metric.getTags() + ";" : "")
                + metric.getStat() + "[" + metric.getType() + "]"
                + (metric.getUnit().isEmpty() ? "" : "(" + metric.getUnit() + ")");
    }

    interface KeyStageBuilder {
        @Nonnull
        TagsStageBuilder key(@Nonnull String key);

        @Nonnull
        TagsStageBuilder key(@Nonnull Key key);
    }

    interface TagsStageBuilder extends Factory {
        @Nonnull
        default MetaStageBuilder empty() {
            return tags(Tags.empty());
        }

        @Nonnull
        MetaStageBuilder tag(@Nonnull String key, @Nonnull String value);

        @Nonnull
        MetaStageBuilder tags(@Nonnull Tags tags);
    }

    interface MetaStageBuilder extends Factory {
        @Nonnull
        default Factory empty() {
            return meta(Tags.empty());
        }

        @Nonnull
        Factory meta(@Nonnull String key, @Nonnull String value);

        @Nonnull
        Factory meta(@Nonnull Tags meta);
    }

    interface Factory {
        @Nonnull
        default Metric counter() {
            return counter("");
        }

        @Nonnull
        default Metric counter(@Nonnull final String unit) {
            return metric(Stat.VALUE, Type.COUNTER, unit);
        }

        @Nonnull
        default Metric min() {
            return min("");
        }

        @Nonnull
        default Metric min(@Nonnull final String unit) {
            return metric(Stat.MIN, Type.AGGREGATE, unit);
        }

        @Nonnull
        default Metric max() {
            return max("");
        }

        @Nonnull
        default Metric max(@Nonnull final String unit) {
            return metric(Stat.MAX, Type.AGGREGATE, unit);
        }

        @Nonnull
        default Metric mean() {
            return mean("");
        }

        @Nonnull
        default Metric mean(@Nonnull final String unit) {
            return metric(Stat.MEAN, Type.AGGREGATE, unit);
        }

        @Nonnull
        default Metric std() {
            return std("");
        }

        @Nonnull
        default Metric std(@Nonnull final String unit) {
            return metric(Stat.STD, Type.AGGREGATE, unit);
        }

        @Nonnull
        default Metric percentile(final double quantile) {
            return percentile(quantile, "");
        }

        @Nonnull
        default Metric percentile(final double quantile, @Nonnull final String unit) {
            return metric(Percentile.of(quantile), Type.AGGREGATE, unit);
        }

        @Nonnull
        default Metric percentile(final int percentile) {
            return percentile(percentile, "");
        }

        @Nonnull
        default Metric percentile(final int percentile, @Nonnull final String unit) {
            return metric(Percentile.of(percentile), Type.AGGREGATE, unit);
        }

        @Nonnull
        default Metric gauge() {
            return gauge("");
        }

        @Nonnull
        default Metric gauge(@Nonnull final String unit) {
            return metric(Stat.VALUE, Type.GAUGE, unit);
        }

        @Nonnull
        Metric metric(@Nonnull Stat stat, @Nonnull Type type, @Nonnull String unit);
    }
}
