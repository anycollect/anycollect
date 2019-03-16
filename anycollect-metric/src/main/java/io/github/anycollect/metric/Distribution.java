package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public interface Distribution {
    Distribution NOOP = amount -> { };

    static DistributionBuilder key(@Nonnull final String key) {
        return new DistributionBuilder(key);
    }

    static DistributionBuilder key(@Nonnull final String... keyParts) {
        return new DistributionBuilder(keyParts);
    }

    void record(long amount);

    final class DistributionBuilder extends BaseMeterBuilder<DistributionBuilder> {
        DistributionBuilder(@Nonnull final String value) {
            super(value);
        }

        public DistributionBuilder(@Nonnull final String... keyParts) {
            super(keyParts);
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
