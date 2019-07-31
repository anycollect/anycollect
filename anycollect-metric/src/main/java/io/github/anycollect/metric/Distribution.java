package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public interface Distribution {
    Distribution NOOP = amount -> { };

    static DistributionBuilder key(@Nonnull final String key) {
        return new DistributionBuilder(Key.of(key));
    }

    static DistributionBuilder key(@Nonnull final Key key) {
        return new DistributionBuilder(key);
    }

    void record(long amount);

    final class DistributionBuilder extends BaseMeterBuilder<DistributionBuilder> {
        DistributionBuilder(@Nonnull final Key key) {
            super(key);
        }

        @Override
        protected DistributionBuilder self() {
            return this;
        }

        public DistributionBuilder unit(@Nonnull final String unit) {
            return super.unit(unit);
        }

        public Distribution register(@Nonnull final MeterRegistry registry) {
            ImmutableMeterId id = new ImmutableMeterId(
                    getKey(), getUnit(),
                    getTagsBuilder().build(), getMetaBuilder().build());
            return registry.distribution(id);
        }
    }
}
