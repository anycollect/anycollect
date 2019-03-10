package io.github.anycollect.readers.jmx.discovery;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.target.TargetCreationException;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.readers.jmx.config.JavaAppConfig;
import io.github.anycollect.readers.jmx.server.JavaApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Static Java App discovery
 */
@Extension(name = StaticJavaAppDiscovery.NAME, point = JavaAppDiscovery.class)
public final class StaticJavaAppDiscovery implements JavaAppDiscovery {
    public static final String NAME = "StaticJavaAppDiscovery";
    private static final Logger LOG = LoggerFactory.getLogger(StaticJavaAppDiscovery.class);
    private final Set<JavaApp> apps;

    @ExtCreator
    public StaticJavaAppDiscovery(@ExtConfig @Nonnull final Config config) {
        // TODO inject
        JavaAppFactory factory = new DefaultJavaAppFactory(config.registry);
        apps = new HashSet<>();
        for (JavaAppConfig appConfig : config.appConfigs) {
            try {
                apps.add(factory.create(appConfig));
            } catch (TargetCreationException e) {
                LOG.warn("could not create java target from definition: {}", appConfig, e);
            }
        }
    }

    @Override
    public Set<JavaApp> discover() {
        return Collections.unmodifiableSet(apps);
    }

    public static class Config {
        private final MeterRegistry registry;
        private final List<JavaAppConfig> appConfigs;

        @JsonCreator
        public Config(@JacksonInject @Nonnull final MeterRegistry registry,
                      @JsonProperty("instances") @Nonnull final List<JavaAppConfig> appConfigs) {
            this.registry = registry;
            this.appConfigs = appConfigs;
        }
    }
}
