package io.github.anycollect.core.api.target;

import io.github.anycollect.core.api.query.SelfQuery;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import java.util.List;

public final class SelfTarget extends AbstractTarget<SelfQuery> {
    public SelfTarget(@Nonnull final String id, @Nonnull final Tags tags, @Nonnull final Tags meta) {
        super(id, tags, meta);
    }

    @Override
    public List<Metric> execute(@Nonnull final SelfQuery query) {
        return query.executeOn(this);
    }
}
