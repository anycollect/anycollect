package io.github.anycollect.core.api.reframe;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.metric.frame.MetricFrame;
import io.github.anycollect.metric.frame.Reframer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Enricher implements Reframer {
    private final String prefix;
    private final Tags tags;
    private final Tags meta;

    public Enricher(@Nullable final String prefix,
                    @Nonnull final Tags tags, @Nonnull final Tags meta,
                    @Nonnull final Target target, @Nonnull final Query query) {
        this.prefix = prefix;
        this.tags = tags.concat(target.getTags()).concat(query.getTags());
        this.meta = meta.concat(target.getMeta()).concat(query.getMeta());
    }

    @Nonnull
    @Override
    public MetricFrame reframe(@Nonnull final MetricFrame source) {
        return source.prefix(prefix).frontTags(tags).frontMeta(meta);
    }
}
