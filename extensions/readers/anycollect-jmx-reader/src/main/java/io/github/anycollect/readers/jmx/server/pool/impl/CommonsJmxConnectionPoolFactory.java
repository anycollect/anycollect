package io.github.anycollect.readers.jmx.server.pool.impl;

import io.github.anycollect.readers.jmx.server.JmxConnection;
import io.github.anycollect.readers.jmx.server.JmxConnectionFactory;
import io.github.anycollect.readers.jmx.server.pool.AsyncConnectionCloser;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPoolFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

public final class CommonsJmxConnectionPoolFactory implements JmxConnectionPoolFactory {
    private static final int MAX_WAIT_SECONDS = 20;
    private static final int MINUTES_BETWEEN_EVICTION_RUNS = 5;
    private static final int MIN_EVICTABLE_IDLE_MINUTES = 5;
    @Nonnull
    private final AsyncConnectionCloser closer;
    @Nonnull
    private final GenericObjectPoolConfig<JmxConnection> config;

    public CommonsJmxConnectionPoolFactory() {
        this(new AsyncConnectionCloser(Executors.newSingleThreadExecutor()));
    }

    public CommonsJmxConnectionPoolFactory(@Nonnull final AsyncConnectionCloser closer) {
        Objects.requireNonNull(closer, "async connection closer must not be null");
        this.closer = closer;
        config = new GenericObjectPoolConfig<>();
        config.setMinIdle(-1);
        config.setMaxIdle(-1);
        config.setMaxTotal(-1);
        config.setTimeBetweenEvictionRunsMillis(MINUTES.toMillis(MINUTES_BETWEEN_EVICTION_RUNS));
        config.setMinEvictableIdleTimeMillis(MINUTES.toMillis(MIN_EVICTABLE_IDLE_MINUTES));
        config.setMaxWaitMillis(SECONDS.toMillis(MAX_WAIT_SECONDS));
        config.setBlockWhenExhausted(true);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        config.setTestOnCreate(true);
    }

    @Nonnull
    @Override
    public JmxConnectionPool create(@Nonnull final JmxConnectionFactory jmxConnectionFactory) {
        CommonsJmxConnectionFactoryAdapter factory
                = new CommonsJmxConnectionFactoryAdapter(jmxConnectionFactory, closer);
        GenericObjectPool<JmxConnection> commonsPool = new GenericObjectPool<>(factory, config);
        return new CommonsJmxConnectionPool(commonsPool);
    }
}
