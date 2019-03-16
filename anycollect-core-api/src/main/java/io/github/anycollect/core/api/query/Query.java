package io.github.anycollect.core.api.query;

import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;

public interface Query {
    @Nonnull
    String getId();

    @Nonnull
    Tags getTags();

    @Nonnull
    Tags getMeta();
}
