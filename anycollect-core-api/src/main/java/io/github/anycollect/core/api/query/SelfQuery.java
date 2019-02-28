package io.github.anycollect.core.api.query;

import io.github.anycollect.core.api.target.SelfTarget;
import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class SelfQuery extends AbstractQuery {
    public SelfQuery(@Nonnull final String id) {
        super(id);
    }

    public abstract List<MetricFamily> executeOn(@Nonnull SelfTarget target);
}
