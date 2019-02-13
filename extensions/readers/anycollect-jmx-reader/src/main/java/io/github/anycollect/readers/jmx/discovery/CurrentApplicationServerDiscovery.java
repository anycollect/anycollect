package io.github.anycollect.readers.jmx.discovery;

import io.github.anycollect.readers.jmx.application.Application;
import io.github.anycollect.readers.jmx.application.ApplicationRegistry;
import io.github.anycollect.readers.jmx.server.JmxConnection;
import io.github.anycollect.readers.jmx.server.JmxConnectionFactory;
import io.github.anycollect.readers.jmx.server.Server;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPoolFactory;

import javax.annotation.Nonnull;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public final class CurrentApplicationServerDiscovery implements ServerDiscovery {
    private static final JmxConnectionFactory JMX_CONNECTION_FACTORY = new JmxConnectionFactory() {
        @Nonnull
        @Override
        public JmxConnection createJmxConnection() {
            return new JmxConnection(null, ManagementFactory.getPlatformMBeanServer());
        }
    };
    private final String currentApplicationName;
    private final JmxConnectionPoolFactory poolFactory;

    public CurrentApplicationServerDiscovery(@Nonnull final String currentApplicationName,
                                             @Nonnull final JmxConnectionPoolFactory poolFactory) {
        Objects.requireNonNull(currentApplicationName, "current application name must not be null");
        Objects.requireNonNull(poolFactory, "pool factory must not be null");
        this.currentApplicationName = currentApplicationName;
        this.poolFactory = poolFactory;
    }

    @Nonnull
    @Override
    public Set<Server> getServers(@Nonnull final ApplicationRegistry registry) throws DiscoverException {
        return Collections.singleton(getServer(registry));
    }

    public Server getServer(@Nonnull final ApplicationRegistry registry) throws DiscoverException {
        if (!registry.hasApplication(currentApplicationName)) {
            throw new DiscoverException("there is no information about " + currentApplicationName + " in registry");
        }
        Application application = registry.getApplication(currentApplicationName);
        JmxConnectionPool pool = poolFactory.create(JMX_CONNECTION_FACTORY);
        return Server.create(currentApplicationName, application, pool);
    }
}
