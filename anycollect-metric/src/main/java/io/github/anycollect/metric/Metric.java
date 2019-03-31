package io.github.anycollect.metric;

import io.github.anycollect.metric.frame.MetricFrame;
import io.github.anycollect.metric.frame.Reframer;
import io.github.anycollect.metric.prepared.PreparedMetricBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Metric {
    static Builder builder() {
        return new Builder();
    }

    static PreparedMetricBuilder prepare() {
        return new PreparedMetricBuilder();
    }

    static Metric of(@Nonnull String key,
                     @Nonnull Tags tags,
                     @Nonnull Tags meta,
                     @Nonnull Measurement measurement,
                     long timestamp) {
        return of(key, tags, meta, Collections.singletonList(measurement), timestamp);
    }

    static Metric of(@Nonnull String key,
                     @Nonnull Tags tags,
                     @Nonnull Tags meta,
                     @Nonnull List<Measurement> measurements,
                     long timestamp) {
        return new ImmutableMetric(
                key, timestamp, measurements, tags, meta
        );
    }

    static Metric empty(@Nonnull MeterId id, long timestamp) {
        return of(id, Collections.emptyList(), timestamp);
    }

    static Metric of(@Nonnull MeterId id, Measurement measurement, long timestamp) {
        return of(id, Collections.singletonList(measurement), timestamp);
    }

    static Metric of(@Nonnull MeterId id, List<Measurement> measurements, long timestamp) {
        return new ImmutableMetric(id.getKey(), timestamp,
                measurements, id.getTags(), id.getMetaTags());
    }

    @Nonnull
    List<? extends Measurement> getMeasurements();

    @Nonnull
    default String getKey() {
        return getFrame().getKey();
    }

    @Nonnull
    default Tags getTags() {
        return getFrame().getTags();
    }

    @Nonnull
    default Tags getMeta() {
        return getFrame().getMeta();
    }

    @Nonnull
    MetricFrame getFrame();

    long getTimestamp();

    int size();

    @Nonnull
    Metric reframe(@Nonnull MetricFrame frame);

    @Nonnull
    default Metric reframe(@Nonnull Reframer reframer) {
        return reframe(reframer.reframe(getFrame()));
    }

    @Nonnull
    default Metric prefix(@Nullable final String prefix) {
        return reframe(getFrame().prefix(prefix));
    }

    @Nonnull
    default Metric frontTags(@Nonnull final Tags tags) {
        return reframe(getFrame().frontTags(tags));
    }

    @Nonnull
    default Metric backTags(@Nonnull final Tags tags) {
        return reframe(getFrame().backTags(tags));
    }

    @Nonnull
    default Metric frontMeta(@Nonnull final Tags meta) {
        return reframe(getFrame().frontMeta(meta));
    }

    @Nonnull
    default Metric backMeta(@Nonnull final Tags meta) {
        return reframe(getFrame().backMeta(meta));
    }

    @Nonnull
    default Metric removeTag(@Nonnull final String key) {
        return reframe(getFrame().removeTag(key));
    }

    class Builder extends BaseBuilder<Builder> {
        private long timestamp = -1;
        private final List<Measurement> measurements = new ArrayList<>();

        @Override
        protected Builder self() {
            return this;
        }

        public Builder key(@Nonnull final String key) {
            return super.key(key);
        }

        @Override
        public Builder key(@Nonnull final String... keyParts) {
            return super.key(keyParts);
        }

        public Builder at(final long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder counter(final double value) {
            return counter("", value);
        }

        public Builder counter(@Nonnull final String unit, final double value) {
            return measurement(Stat.VALUE, Type.COUNTER, unit, value);
        }

        public Builder gauge(final double value) {
            return gauge("", value);
        }

        public Builder gauge(@Nonnull final String unit, final double value) {
            return measurement(Stat.VALUE, Type.GAUGE, unit, value);
        }

        public Builder measurement(@Nonnull final Stat stat,
                                   @Nonnull final Type type,
                                   @Nonnull final String unit,
                                   final double value) {
            measurements.add(new ImmutableMeasurement(stat, type, unit, value));
            return this;
        }

        public Metric build() {
            if (timestamp == -1) {
                throw new IllegalStateException("timestamp must be set");
            }
            return new ImmutableMetric(getKey(), timestamp, measurements,
                    getTagsBuilder().build(), getMetaBuilder().build());
        }
    }
}
