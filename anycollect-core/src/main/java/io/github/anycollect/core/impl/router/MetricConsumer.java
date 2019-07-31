package io.github.anycollect.core.impl.router;

import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;
import java.util.List;

public interface MetricConsumer extends RouterNode {
    void consume(@Nonnull List<? extends Sample> samples);

    void stop();
}
