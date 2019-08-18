package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.core.api.query.QueryProvider;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Extension(name = JvmMetrics.NAME, contracts = QueryProvider.class)
public final class JvmMetrics implements QueryProvider<JmxQuery> {
    public static final String NAME = "JvmMetrics";
    private final Set<JmxQuery> queries;

    @ExtCreator
    public JvmMetrics(@ExtConfig(optional = true) @Nullable final JvmMetricsConfig optConfig) {
        JvmMetricsConfig config;
        if (optConfig == null) {
            config = JvmMetricsConfig.DEFAULT;
        } else {
            config = optConfig;
        }
        queries = new HashSet<>();
        queries.add(new JvmMemory(config.prefix(), config.tags(), config.meta()));
        queries.add(new JvmThreads(config.prefix(), config.tags(), config.meta()));
        queries.add(new JvmRuntime(config.prefix(), config.tags(), config.meta()));
        queries.add(new JvmGc(config.prefix(), config.tags(), config.meta()));
    }

    @Nonnull
    @Override
    public Set<JmxQuery> provide() {
        return Collections.unmodifiableSet(queries);
    }
}
