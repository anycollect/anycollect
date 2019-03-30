package io.github.anycollect.core.api.job;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.reframe.Enricher;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.metric.frame.Reframer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public final class TaggingJob implements Job {
    private final Reframer reframer;
    private final Job delegate;

    public TaggingJob(@Nonnull final Target target, @Nonnull final Query query,
                      @Nonnull final Job delegate) {
        this(null, target, query, delegate);
    }

    public TaggingJob(@Nullable final String prefix,
                      @Nonnull final Target target, @Nonnull final Query query,
                      @Nonnull final Job delegate) {
        this(prefix, Tags.empty(), Tags.empty(), target, query, delegate);
    }

    public TaggingJob(@Nullable final String prefix,
                      @Nonnull final Tags tags, @Nonnull final Tags meta,
                      @Nonnull final Target target, @Nonnull final Query query,
                      @Nonnull final Job delegate) {
        this.reframer = new Enricher(prefix, tags, meta, target, query);
        this.delegate = delegate;
    }

    @Override
    public List<Metric> execute() throws QueryException, ConnectionException {
        return delegate.execute().stream()
                .map(metric -> metric.reframe(reframer))
                .collect(Collectors.toList());
    }
}
