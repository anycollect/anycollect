package io.github.anycollect.core.impl.router;

import io.github.anycollect.core.api.filter.Filter;
import io.github.anycollect.core.api.filter.FilterReply;
import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.stream.Collectors.toList;

public final class FilteredMetricConsumer implements MetricConsumer {
    private final Filter filter;
    private final MetricConsumer delegate;

    public FilteredMetricConsumer(@Nonnull final Filter filter,
                                  @Nonnull final MetricConsumer delegate) {
        this.filter = filter;
        this.delegate = delegate;
    }

    @Override
    public void consume(@Nonnull final List<? extends Sample> samples) {
        List<? extends Sample> filtered = samples.stream()
                .filter(sample -> filter.accept(sample.getMetric()) != FilterReply.DENY)
                .collect(toList());
        delegate.consume(filtered);
    }

    @Override
    public void stop() {
        delegate.stop();
    }

    @Nonnull
    @Override
    public String getAddress() {
        return delegate.getAddress();
    }

    @Override
    public String toString() {
        return getAddress();
    }
}
