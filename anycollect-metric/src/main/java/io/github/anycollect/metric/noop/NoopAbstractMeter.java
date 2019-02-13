package io.github.anycollect.metric.noop;

import io.github.anycollect.metric.Meter;
import io.github.anycollect.metric.MeterId;
import io.github.anycollect.metric.Metric;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

@EqualsAndHashCode(of = "id")
public class NoopAbstractMeter implements Meter {
    private final MeterId id;

    public NoopAbstractMeter(@Nonnull final MeterId id) {
        this.id = id;
    }

    @Override
    public final MeterId getId() {
        return id;
    }

    @Override
    public final Stream<Metric> measure() {
        return Stream.empty();
    }
}
