package io.github.anycollect.readers.jmx;

import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.HealthCheckConfig;
import io.github.anycollect.core.api.internal.PullManager;
import io.github.anycollect.core.api.internal.QueryMatcherResolver;
import io.github.anycollect.core.api.query.QueryProvider;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.annotations.InstanceId;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.jmx.query.JmxQuery;
import io.github.anycollect.readers.jmx.server.JavaApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;

@Extension(name = JmxReader.NAME, point = Reader.class)
public class JmxReader implements Reader, Lifecycle {
    public static final String NAME = "JmxReader";
    private static final Logger LOG = LoggerFactory.getLogger(JmxReader.class);
    private final PullManager puller;
    private final ServiceDiscovery<JavaApp> discovery;
    private final QueryProvider<JmxQuery> queries;
    private final QueryMatcherResolver matcher;
    private final String id;

    @ExtCreator
    public JmxReader(
            @ExtDependency(qualifier = "puller") @Nonnull final PullManager puller,
            @ExtDependency(qualifier = "discovery") @Nonnull final List<ServiceDiscovery<JavaApp>> discovery,
            @ExtDependency(qualifier = "queries") @Nonnull final List<QueryProvider<JmxQuery>> queries,
            @ExtDependency(qualifier = "matcher") @Nonnull final QueryMatcherResolver matcher,
            @InstanceId @Nonnull final String id) {
        this.puller = puller;
        this.discovery = ServiceDiscovery.composite(discovery);
        this.matcher = matcher;
        this.queries = QueryProvider.composite(queries);
        this.id = id;
    }

    @Override
    public void start(@Nonnull final Dispatcher dispatcher) {
        puller.start(discovery, queries, matcher, dispatcher, HealthCheckConfig.builder().tags(Tags.of("check", "jmx")).build());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void init() {
        LOG.info("{} has been successfully initialised", NAME);
    }

    @Override
    public void destroy() {
        LOG.info("{}({}) has been successfully destroyed", id, NAME);
    }
}
