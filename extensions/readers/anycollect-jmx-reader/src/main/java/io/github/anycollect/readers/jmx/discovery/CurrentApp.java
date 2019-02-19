package io.github.anycollect.readers.jmx.discovery;

import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.readers.jmx.server.JavaApp;
import io.github.anycollect.readers.jmx.server.JmxConnection;
import io.github.anycollect.readers.jmx.server.JmxConnectionFactory;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPoolFactory;
import io.github.anycollect.readers.jmx.server.pool.impl.CommonsJmxConnectionPoolFactory;

import javax.annotation.Nonnull;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.Set;

@Extension(name = "CurrentApp", point = JavaAppDiscovery.class)
public final class CurrentApp implements JavaAppDiscovery {
    private static final JmxConnectionFactory JMX_CONNECTION_FACTORY = new JmxConnectionFactory() {
        @Nonnull
        @Override
        public JmxConnection createJmxConnection() {
            return new JmxConnection(null, ManagementFactory.getPlatformMBeanServer());
        }
    };
    private final Set<JavaApp> app;

    @ExtCreator
    public CurrentApp(@ExtConfig(key = "name") @Nonnull final String currentApplicationName) {
        JmxConnectionPoolFactory poolFactory = new CommonsJmxConnectionPoolFactory();
        JmxConnectionPool pool = poolFactory.create(JMX_CONNECTION_FACTORY);
        app = Collections.singleton(JavaApp.create(currentApplicationName, pool));
    }

    @Override
    public Set<JavaApp> discover() {
        return app;
    }
}
