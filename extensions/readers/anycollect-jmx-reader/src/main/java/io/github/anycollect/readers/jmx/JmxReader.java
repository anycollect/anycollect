package io.github.anycollect.readers.jmx;

import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.PullManager;
import io.github.anycollect.core.api.internal.QueryMatcherResolver;
import io.github.anycollect.core.api.query.QueryProvider;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.annotations.InstanceId;
import io.github.anycollect.readers.jmx.discovery.JavaAppDiscovery;
import io.github.anycollect.readers.jmx.query.JmxHealthCheck;
import io.github.anycollect.readers.jmx.query.JmxQuery;
import io.github.anycollect.readers.jmx.query.JmxQueryProvider;
import io.github.anycollect.readers.jmx.server.JavaApp;

import javax.annotation.Nonnull;
import java.util.List;

@Extension(name = JmxReader.NAME, point = Reader.class)
public class JmxReader implements Reader {
    public static final String NAME = "JmxReader";
    private final PullManager puller;
    private final ServiceDiscovery<JavaApp> discovery;
    private final QueryProvider<JmxQuery> queries;
    private final QueryMatcherResolver matcher;
    private final String id;

    @ExtCreator
    public JmxReader(@ExtDependency(qualifier = "puller") @Nonnull final PullManager puller,
                     @ExtDependency(qualifier = "discovery") @Nonnull final List<JavaAppDiscovery> discovery,
                     @ExtDependency(qualifier = "queries") @Nonnull final List<JmxQueryProvider> queries,
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
        puller.start(discovery, queries, matcher, dispatcher, new JmxHealthCheck());
    }

    @Override
    public String getId() {
        return id;
    }
}
