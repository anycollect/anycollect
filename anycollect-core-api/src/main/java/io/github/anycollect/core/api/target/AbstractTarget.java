package io.github.anycollect.core.api.target;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.metric.Tags;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import java.util.Objects;

@EqualsAndHashCode
public abstract class AbstractTarget<Q extends Query> implements Target<Q> {
    private final String id;
    private final Tags tags;

    public AbstractTarget(@Nonnull final String id, @Nonnull final Tags tags) {
        Objects.requireNonNull(id, "instance id must not be null");
        Objects.requireNonNull(tags, "tags must not be null");
        this.id = id;
        this.tags = tags;
    }

    @Nonnull
    @Override
    public final String getId() {
        return id;
    }

    @Nonnull
    @Override
    public final Tags getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return id;
    }
}
