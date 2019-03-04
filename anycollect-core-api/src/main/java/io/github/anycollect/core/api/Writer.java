package io.github.anycollect.core.api;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;

public interface Writer extends Route {
    void write(@Nonnull List<Metric> families);
}
