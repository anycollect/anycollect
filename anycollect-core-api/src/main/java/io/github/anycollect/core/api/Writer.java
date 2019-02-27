package io.github.anycollect.core.api;

import io.github.anycollect.extensions.annotations.ExtPoint;
import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import java.util.List;

@ExtPoint
public interface Writer {
    void write(@Nonnull List<MetricFamily> families);
}
