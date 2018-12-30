package io.github.anycollect.readers.jmx.application;

import io.github.anycollect.readers.jmx.query.Query;

import javax.annotation.Nonnull;

public final class AllQueryMatcher implements QueryMatcher {
    @Override
    public boolean matches(@Nonnull final Query query) {
        return true;
    }
}
