package io.github.anycollect.readers.process;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.kv.KeyValue;
import io.github.anycollect.core.api.target.CachedKvDiscovery;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;

import javax.annotation.Nonnull;
import java.util.Set;

@Extension(name = KvProcessDiscovery.NAME, point = ProcessDiscovery.class)
public final class KvProcessDiscovery implements ProcessDiscovery {
    public static final String NAME = "KvProcessDiscovery";
    private final ServiceDiscovery<Process> delegate;

    @ExtCreator
    public KvProcessDiscovery(@ExtDependency(qualifier = "kv") @Nonnull final KeyValue kv,
                              @ExtConfig @Nonnull final Config config) {
        this.delegate = CachedKvDiscovery.create(kv, Process.class, config.key);
    }

    @Override
    public Set<Process> discover() {
        return delegate.discover();
    }

    public static final class Config {
        private final String key;

        @JsonCreator
        public Config(@JsonProperty(value = "key", required = true) @Nonnull final String key) {
            this.key = key;
        }
    }
}
