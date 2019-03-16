package io.github.anycollect.core.api.query;

import io.github.anycollect.metric.Tags;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;

@EqualsAndHashCode
public abstract class AbstractQuery implements Query {
    private final String id;
    private final Tags tags;
    private final Tags meta;

    public AbstractQuery(@Nonnull final String id) {
        this(id, Tags.empty(), Tags.empty());
    }

    public AbstractQuery(@Nonnull final String id, @Nonnull final Tags tags, @Nonnull final Tags meta) {
        this.id = id;
        this.tags = tags;
        this.meta = meta;
    }

    @Nonnull
    @Override
    public final String getId() {
        return id;
    }

    @Nonnull
    @Override
    public Tags getTags() {
        return tags;
    }

    @Nonnull
    @Override
    public Tags getMeta() {
        return meta;
    }

    @Override
    public String toString() {
        return id;
    }
}
