package io.github.anycollect.readers.jmx.module;

import io.github.anycollect.readers.jmx.query.Query;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Set;

@ThreadSafe
public interface QueryModule {
    Set<Query> getQueries();
}
