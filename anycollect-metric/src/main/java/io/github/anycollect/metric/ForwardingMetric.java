package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class ForwardingMetric implements Metric {
    private final Metric delegate;

    public ForwardingMetric(@Nonnull final Metric delegate) {
        this.delegate = delegate;
    }

    @Nonnull
    @Override
    public String getKey() {
        return delegate.getKey();
    }

    @Override
    public long getTimestamp() {
        return delegate.getTimestamp();
    }

    @Nonnull
    @Override
    public List<? extends Measurement> getMeasurements() {
        return delegate.getMeasurements();
    }

    @Nonnull
    @Override
    public Tags getTags() {
        return delegate.getTags();
    }

    @Nonnull
    @Override
    public Tags getMeta() {
        return delegate.getMeta();
    }
}
