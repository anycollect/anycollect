package io.github.anycollect.core.api.target;

import io.github.anycollect.core.api.query.SelfQuery;
import io.github.anycollect.metric.MetricFamily;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import java.util.List;

public final class SelfTarget extends AbstractTarget<SelfQuery> {
    public SelfTarget(@Nonnull final String id, @Nonnull final Tags tags) {
        super(id, tags);
    }

    @Override
    public List<MetricFamily> execute(@Nonnull final SelfQuery query) {
        return query.executeOn(this);
    }
}
