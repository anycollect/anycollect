package io.github.anycollect.readers.jmx.processing;

import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.Server;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Set;

@ThreadSafe
public interface QueryScheduler {
    void schedule(@Nonnull Set<Server> servers, @Nonnull Set<Query> queries);
}
