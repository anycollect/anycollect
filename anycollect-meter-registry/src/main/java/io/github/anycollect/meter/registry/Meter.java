package io.github.anycollect.meter.registry;

import io.github.anycollect.metric.MeterId;
import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;
import java.util.List;

public interface Meter {
    @Nonnull
    MeterId getId();

    List<Sample> measure();
}
