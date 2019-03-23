package io.github.anycollect.core.api.job;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.ImmutableMetric;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class TaggingJob implements Job {
    private final Tags tags;
    private final Tags meta;
    private final Job delegate;

    public TaggingJob(@Nonnull final Tags tags, @Nonnull final Tags meta, @Nonnull final Job delegate) {
        this.tags = tags;
        this.meta = meta;
        this.delegate = delegate;
    }

    @Override
    public List<Metric> execute() throws QueryException, ConnectionException {
        List<Metric> metrics = delegate.execute();
        List<Metric> taggedMetrics = new ArrayList<>(metrics.size());
        for (Metric metric : metrics) {
            ImmutableMetric tagged = new ImmutableMetric(
                    metric.getKey(),
                    metric.getTimestamp(),
                    metric.getMeasurements(),
                    tags.isEmpty() ? metric.getTags() : Tags.concat(tags, metric.getTags()),
                    meta.isEmpty() ? metric.getMeta() : Tags.concat(meta, metric.getMeta())
            );
            taggedMetrics.add(tagged);
        }
        return taggedMetrics;
    }
}
