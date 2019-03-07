package io.github.anycollect.readers.jmx.discovery;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.kv.KeyValue;
import io.github.anycollect.core.api.target.CachedKvDiscovery;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.readers.jmx.config.JavaAppConfig;
import io.github.anycollect.readers.jmx.server.JavaApp;

import javax.annotation.Nonnull;
import java.util.*;

@Extension(name = KvJavaAppDiscovery.NAME, point = JavaAppDiscovery.class)
public final class KvJavaAppDiscovery implements JavaAppDiscovery {
    public static final String NAME = "KvJavaAppDiscovery";
    private final ServiceDiscovery<JavaApp> delegate;

    @ExtCreator
    public KvJavaAppDiscovery(@ExtDependency(qualifier = "kv") @Nonnull final KeyValue kv,
                              @ExtConfig @Nonnull final Config config) {
        this.delegate = new CachedKvDiscovery<>(
                kv,
                JavaAppConfig.class,
                new DefaultJavaAppFactory(config.registry),
                config.key
        );
    }

    @Override
    public synchronized Set<JavaApp> discover() {
        return delegate.discover();
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
