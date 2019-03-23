package io.github.anycollect.core.api.target;

import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;

public interface Target<Q extends Query> {
    /**
     * Instance id for application that can unique identify the target.
     * It can be for example in "127.0.0.1:8080" or "users-123" formats.
     * This value should be unique across all monitored by anycollect targets.
     *
     * @return instance id
     */
    @Nonnull
    String getId();

    @Nonnull
    Tags getTags();

    @Nonnull
    Tags getMeta();

    @Nonnull
    Job bind(@Nonnull Q query);
}
