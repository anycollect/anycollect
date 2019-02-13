package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public interface DistributionSummary extends Meter {
    static Builder key(@Nonnull final String key) {
        return new Builder(key);
    }

    void record(double amount);

    long count();

    double totalAmount();

    default double mean() {
        return count() == 0 ? 0 : totalAmount() / count();
    }

    double max();

    class Builder {
        private final ImmutableMeterId.Builder idBuilder = new ImmutableMeterId.Builder();
        private double[] percentiles;

        public Builder(@Nonnull final String key) {
            idBuilder.key(key);
        }

        public Builder unit(@Nonnull final String value) {
            idBuilder.unit(value);
            return this;
        }

        public Builder tags(@Nonnull final String key, @Nonnull final String value) {
            idBuilder.tag(key, value);
            return this;
        }

        public Builder meta(@Nonnull final String key, @Nonnull final String value) {
            idBuilder.meta(key, value);
            return this;
        }

        public Builder percentiles(final double... percentileValues) {
            this.percentiles = percentileValues;
            return this;
        }

        public DistributionSummary register(@Nonnull final MeterRegistry registry) {
            return registry.summary(idBuilder.build(), percentiles);
        }
    }
}
