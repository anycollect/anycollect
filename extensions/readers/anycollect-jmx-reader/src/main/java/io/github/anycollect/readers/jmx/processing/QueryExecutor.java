package io.github.anycollect.readers.jmx.processing;

import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.Server;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface QueryExecutor {
    void submit(@Nonnull Query query, @Nonnull Server server);
}
