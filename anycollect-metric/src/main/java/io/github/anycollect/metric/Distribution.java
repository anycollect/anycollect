package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public interface Distribution extends Meter {
    static DistributionBuilder key(@Nonnull final String key) {
        return new DistributionBuilder(key);
    }

    void record(double amount);

    final class DistributionBuilder extends BaseMeterBuilder<DistributionBuilder> {
        DistributionBuilder(@Nonnull final String value) {
            super(value);
        }

        @Override
        protected DistributionBuilder self() {
            return this;
        }

        public Distribution register(@Nonnull final MeterRegistry registry) {
            ImmutableMeterId id = new ImmutableMeterId(
                    getKey(), getUnit(),
                    getTagsBuilder().build(), getMetaBuilder().build());
            return registry.distribution(id);
        }
    }
}
