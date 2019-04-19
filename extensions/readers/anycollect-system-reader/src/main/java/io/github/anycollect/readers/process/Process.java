package io.github.anycollect.readers.process;

import io.github.anycollect.core.api.target.AbstractTarget;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;

public abstract class Process extends AbstractTarget<ProcessQuery> {
    public Process(@Nonnull final String id, @Nonnull final Tags tags, @Nonnull final Tags meta) {
        super(id, tags, meta);
    }
}
