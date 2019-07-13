package io.github.anycollect.core.api.query;

import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;

public interface Query<T extends Target> {
    @Nonnull
    String getId();

    @Nonnull
    Tags getTags();

    @Nonnull
    Tags getMeta();

    @Nonnull
    Job bind(@Nonnull T target);
}
