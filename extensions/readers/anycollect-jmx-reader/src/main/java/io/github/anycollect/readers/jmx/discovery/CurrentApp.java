package io.github.anycollect.readers.jmx.discovery;

import io.github.anycollect.readers.jmx.server.JmxConnection;
import io.github.anycollect.readers.jmx.server.JmxConnectionFactory;
import io.github.anycollect.readers.jmx.server.JavaApp;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPoolFactory;

import javax.annotation.Nonnull;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public final class CurrentApp implements JavaAppDiscovery {
    private static final JmxConnectionFactory JMX_CONNECTION_FACTORY = new JmxConnectionFactory() {
        @Nonnull
        @Override
        public JmxConnection createJmxConnection() {
            return new JmxConnection(null, ManagementFactory.getPlatformMBeanServer());
        }
    };
    private final String currentApplicationName;
    private final JmxConnectionPoolFactory poolFactory;

    public CurrentApp(@Nonnull final String currentApplicationName,
                      @Nonnull final JmxConnectionPoolFactory poolFactory) {
        Objects.requireNonNull(currentApplicationName, "current application name must not be null");
        Objects.requireNonNull(poolFactory, "pool factory must not be null");
        this.currentApplicationName = currentApplicationName;
        this.poolFactory = poolFactory;
    }

    @Override
    public Set<JavaApp> discover() {
        JmxConnectionPool pool = poolFactory.create(JMX_CONNECTION_FACTORY);
        return Collections.singleton(JavaApp.create(currentApplicationName, pool));
    }
}
