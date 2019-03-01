package io.github.anycollect.meter.registry;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.SlidingTimeWindowReservoir;
import com.codahale.metrics.Snapshot;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CodahaleSlidingTimeWindowDistributionSummary extends AbstractMeter implements Distribution, Measurable {
    private final Clock clock;
    private final Histogram histogram;
    private final double[] quantiles;

    public CodahaleSlidingTimeWindowDistributionSummary(@Nonnull final MeterId id,
                                                        final int window,
                                                        @Nonnull final Clock clock,
                                                        @Nonnull final double[] quantiles) {
        super(id);
        this.clock = clock;
        this.quantiles = quantiles;
        SlidingTimeWindowReservoir reservoir = new SlidingTimeWindowReservoir(window, TimeUnit.SECONDS);
        histogram = new Histogram(reservoir);
    }

    @Override
    public void record(final long amount) {
        histogram.update(amount);
    }

    @Nonnull
    @Override
    public List<MetricFamily> measure() {
        Snapshot snapshot = histogram.getSnapshot();
        List<Measurement> measurements = new ArrayList<>();
        for (double quantile : quantiles) {
            double value = snapshot.getValue(quantile);
            Measurement percentile = Measurement.percentile(quantile, value, getId().getUnit());
            measurements.add(percentile);
        }
        measurements.add(Measurement.max(snapshot.getMax(), getId().getUnit()));
        measurements.add(Measurement.mean(snapshot.getMean(), getId().getUnit()));
        measurements.add(Measurement.std(snapshot.getStdDev(), getId().getUnit()));
        return Collections.singletonList(MetricFamily.of(getId(), measurements, clock.wallTime()));
    }
}
