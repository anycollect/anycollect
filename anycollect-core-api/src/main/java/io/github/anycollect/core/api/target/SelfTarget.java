package io.github.anycollect.core.api.target;

import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;

public final class SelfTarget extends AbstractTarget {
    public SelfTarget(@Nonnull final String id) {
        super(id, Tags.empty(), Tags.empty());
    }
}
