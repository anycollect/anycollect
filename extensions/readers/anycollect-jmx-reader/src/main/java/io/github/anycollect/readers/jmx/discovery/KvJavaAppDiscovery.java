package io.github.anycollect.readers.jmx.discovery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.kv.KeyValue;
import io.github.anycollect.core.api.target.CachedKvDiscovery;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.meter.api.MeterRegistry;
import io.github.anycollect.readers.jmx.config.JavaAppConfig;
import io.github.anycollect.readers.jmx.server.JavaApp;

import javax.annotation.Nonnull;
import java.util.Set;

@Extension(name = KvJavaAppDiscovery.NAME, contracts = ServiceDiscovery.class)
public final class KvJavaAppDiscovery implements ServiceDiscovery<JavaApp> {
    public static final String NAME = "KvJavaAppDiscovery";
    private final ServiceDiscovery<JavaApp> delegate;

    @ExtCreator
    public KvJavaAppDiscovery(@ExtDependency(qualifier = "registry") @Nonnull final MeterRegistry registry,
                              @ExtDependency(qualifier = "kv") @Nonnull final KeyValue kv,
                              @ExtConfig @Nonnull final Config config) {
        this.delegate = new CachedKvDiscovery<>(
                kv,
                JavaAppConfig.class,
                new DefaultJavaAppFactory(registry),
                config.key
        );
    }

    @Override
    public synchronized Set<? extends JavaApp> discover() {
        return delegate.discover();
    }

    public static final class Config {
        private final String key;

        @JsonCreator
        public Config(@JsonProperty("key") final String key) {
            this.key = key;
        }
    }
}
