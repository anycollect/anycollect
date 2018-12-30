package io.github.anycollect.readers.jmx.application;

import io.github.anycollect.readers.jmx.query.Query;

import javax.annotation.Nonnull;

public interface QueryMatcher {
    boolean matches(@Nonnull Query query);
}
