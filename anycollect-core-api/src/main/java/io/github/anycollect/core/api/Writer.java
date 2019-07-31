package io.github.anycollect.core.api;

import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

@NotThreadSafe
public interface Writer extends Route {
    void write(@Nonnull List<? extends Sample> metrics);
}
