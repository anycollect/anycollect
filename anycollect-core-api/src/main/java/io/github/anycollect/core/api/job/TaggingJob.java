package io.github.anycollect.core.api.job;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Sample;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public final class TaggingJob implements Job {
    private final Job delegate;
    private final String prefix;
    private final Tags prefixTags;
    private final Tags prefixMeta;

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
        this.prefix = prefix;
        this.prefixTags = tags.concat(target.getTags()).concat(query.getTags());
        this.prefixMeta = meta.concat(target.getMeta()).concat(query.getMeta());
        this.delegate = delegate;
    }

    @Override
    public List<Sample> execute() throws InterruptedException, QueryException, ConnectionException {
        return delegate.execute().stream()
                .map(this::transform)
                .collect(Collectors.toList());
    }

    private Sample transform(final Sample source) {
        return source.getMetric()
                .modify()
                .withPrefix(prefix)
                .frontTags(prefixTags)
                .frontMeta(prefixMeta)
                .commit()
                .sample(source.getValue(), source.getTimestamp());
    }
}
