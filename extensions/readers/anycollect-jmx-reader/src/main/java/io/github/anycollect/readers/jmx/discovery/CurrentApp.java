package io.github.anycollect.readers.jmx.discovery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.jmx.server.JavaApp;
import io.github.anycollect.readers.jmx.server.JmxConnection;
import io.github.anycollect.readers.jmx.server.JmxConnectionFactory;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPoolFactory;
import io.github.anycollect.readers.jmx.server.pool.impl.CommonsJmxConnectionPoolFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.Set;

@Extension(name = "CurrentApp", point = ServiceDiscovery.class)
public final class CurrentApp implements ServiceDiscovery<JavaApp> {
    private static final JmxConnectionFactory JMX_CONNECTION_FACTORY = new JmxConnectionFactory() {
        @Nonnull
        @Override
        public JmxConnection createJmxConnection() {
            return new JmxConnection(null, ManagementFactory.getPlatformMBeanServer());
        }
    };
    private final Set<JavaApp> app;

    @ExtCreator
    public CurrentApp(@ExtDependency(qualifier = "registry") @Nonnull final MeterRegistry registry,
                      @ExtConfig @Nonnull final Config config) {
        JmxConnectionPoolFactory poolFactory = new CommonsJmxConnectionPoolFactory();
        JmxConnectionPool pool = poolFactory.create(JMX_CONNECTION_FACTORY);
        app = Collections.singleton(JavaApp.create(config.targetId,
                config.tags, config.meta, pool, registry));
    }

    @Override
    public Set<JavaApp> discover() {
        return app;
    }

    public static class Config {
        private final String targetId;
        private final Tags tags;
        private final Tags meta;

        @JsonCreator
        public Config(@JsonProperty(value = "targetId", required = true) @Nonnull final String targetId,
                      @JsonProperty("tags") @Nullable final Tags tags,
                      @JsonProperty("meta") @Nullable final Tags meta) {
            this.targetId = targetId;
            this.tags = tags != null ? tags : Tags.empty();
            this.meta = meta != null ? meta : Tags.empty();
        }
    }
}
