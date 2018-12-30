package io.github.anycollect.readers.jmx.module;

import io.github.anycollect.readers.jmx.query.Query;

import java.util.List;

public interface QueryModule {
    List<? extends Query> getQueries();
}
