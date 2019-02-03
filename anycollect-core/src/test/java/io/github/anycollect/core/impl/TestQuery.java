package io.github.anycollect.core.impl;

import io.github.anycollect.core.api.query.AbstractQuery;

import javax.annotation.Nonnull;

public class TestQuery extends AbstractQuery {
    public TestQuery(@Nonnull final String group, @Nonnull final String label) {
        super(group, label);
    }
}
