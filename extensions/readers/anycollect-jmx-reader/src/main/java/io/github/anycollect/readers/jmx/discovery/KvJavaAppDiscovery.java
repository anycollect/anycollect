package io.github.anycollect.readers.jmx.discovery;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.kv.KeyValue;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.readers.jmx.config.JavaAppConfig;
import io.github.anycollect.readers.jmx.server.JavaApp;
import io.github.anycollect.readers.jmx.server.JmxConnectionFactory;
import io.github.anycollect.readers.jmx.server.JmxConnectionFactoryImpl;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPoolFactory;
import io.github.anycollect.readers.jmx.server.pool.impl.CommonsJmxConnectionPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.util.*;

@Extension(name = KvJavaAppDiscovery.NAME, point = JavaAppDiscovery.class)
public final class KvJavaAppDiscovery implements JavaAppDiscovery {
    public static final String NAME = "KvJavaAppDiscovery";
    private static final Logger LOG = LoggerFactory.getLogger(KvJavaAppDiscovery.class);
    private final KeyValue kv;
    private final Config config;
    private final JmxConnectionPoolFactory factory;
    private Map<JavaAppConfig, JavaApp> previous = new HashMap<>();

    @ExtCreator
    public KvJavaAppDiscovery(@ExtDependency(qualifier = "kv") @Nonnull final KeyValue kv,
                              @ExtConfig @Nonnull final Config config) {
        this.kv = kv;
        this.config = config;
        // TODO inject
        factory = new CommonsJmxConnectionPoolFactory();
    }

    @Override
    public synchronized Set<JavaApp> discover() {
        List<JavaAppConfig> javaAppConfigs = kv.getValues(config.key, JavaAppConfig.class);
        Map<JavaAppConfig, JavaApp> apps = new HashMap<>();
        for (JavaAppConfig appConfig : javaAppConfigs) {
            try {
                if (previous.containsKey(appConfig)) {
                    apps.put(appConfig, previous.get(appConfig));
                    continue;
                }
                JmxConnectionFactory connectionFactory = new JmxConnectionFactoryImpl(appConfig);
                JavaApp app = JavaApp.create(
                        appConfig.getInstanceId(),
                        factory.create(connectionFactory),
                        config.registry);
                apps.put(appConfig, app);
            } catch (MalformedURLException e) {
                LOG.warn("given url for {} is malformed: {}", appConfig.getInstanceId(), appConfig.getUrl(), e);
            }
        }
        this.previous = apps;
        HashSet<JavaApp> result = new HashSet<>(apps.values());
        LOG.debug("discovered apps: {}", result);
        return result;
    }

    public static final class Config {
        private final String key;
        private final MeterRegistry registry;

        @JsonCreator
        public Config(@JsonProperty("key") final String key,
                      @JacksonInject final MeterRegistry registry) {
            this.key = key;
            this.registry = registry;
        }
    }
}
