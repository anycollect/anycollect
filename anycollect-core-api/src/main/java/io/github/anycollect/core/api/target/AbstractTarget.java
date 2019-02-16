package io.github.anycollect.core.api.target;

import io.github.anycollect.core.api.query.Query;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import java.util.Objects;

@EqualsAndHashCode
public abstract class AbstractTarget<Q extends Query> implements Target<Q> {
    private final String id;

    public AbstractTarget(@Nonnull final String id) {
        Objects.requireNonNull(id, "instance id must not be null");
        this.id = id;
    }

    @Nonnull
    @Override
    public String getId() {
        return id;
    }
}
