package io.github.anycollect.readers.jmx.discovery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.core.api.target.TargetCreationException;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.meter.api.MeterRegistry;
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
@Extension(name = StaticJavaAppDiscovery.NAME, point = ServiceDiscovery.class)
public final class StaticJavaAppDiscovery implements ServiceDiscovery<JavaApp> {
    public static final String NAME = "StaticJavaAppDiscovery";
    private static final Logger LOG = LoggerFactory.getLogger(StaticJavaAppDiscovery.class);
    private final Set<JavaApp> apps;

    @ExtCreator
    public StaticJavaAppDiscovery(@ExtDependency(qualifier = "registry") @Nonnull final MeterRegistry registry,
                                  @ExtConfig @Nonnull final Config config) {
        // TODO inject
        JavaAppFactory factory = new DefaultJavaAppFactory(registry);
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
        private final List<JavaAppConfig> appConfigs;

        @JsonCreator
        public Config(@JsonProperty("instances") @Nonnull final List<JavaAppConfig> appConfigs) {
            this.appConfigs = appConfigs;
        }
    }
}
