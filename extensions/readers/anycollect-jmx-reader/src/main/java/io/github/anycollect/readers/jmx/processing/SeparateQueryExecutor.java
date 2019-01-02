package io.github.anycollect.readers.jmx.processing;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.github.anycollect.readers.jmx.application.ConcurrencyLevel;
import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;
import java.util.concurrent.*;

@ThreadSafe
public final class SeparateQueryExecutor implements QueryExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(SeparateQueryExecutor.class);
    private final ConcurrentMap<Server, ThreadPoolExecutor> executors = new ConcurrentHashMap<>();
    private final ResultCallback callback;

    public SeparateQueryExecutor(@Nonnull final ResultCallback callback) {
        Objects.requireNonNull(callback, "result callback must not be null");
        this.callback = callback;
    }

    @Override
    public void submit(@Nonnull final Query query, final @Nonnull Server server) {
        ThreadPoolExecutor executor = executors.computeIfAbsent(server, SeparateQueryExecutor::createExecutor);
        executor.submit(new QueryJob(query, server, callback));
    }

    private static ThreadPoolExecutor createExecutor(final Server server) {
        LOG.debug("creating executor for server {}", server);
        int numThreads = server.getApplication()
                .getConcurrencyLevel()
                .map(ConcurrencyLevel::getMaxNumberOfThreads)
                .orElse(1);
        return new ThreadPoolExecutor(numThreads, numThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder()
                        .setDaemon(true)
                        .setNameFormat("JmxReader-" + server.getId() + "-%d")
                        .build()
        );
    }
}
