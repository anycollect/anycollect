package io.github.anycollect.core.api;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

@NotThreadSafe
public interface Writer extends Route {
    void write(@Nonnull List<? extends Metric> metrics);
}
