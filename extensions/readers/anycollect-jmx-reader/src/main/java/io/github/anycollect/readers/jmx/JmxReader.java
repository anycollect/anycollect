package io.github.anycollect.readers.jmx;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.PullManager;
import io.github.anycollect.core.api.internal.QueryMatcherResolver;
import io.github.anycollect.core.api.query.QueryProvider;
import io.github.anycollect.core.api.reader.ServiceReader;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.readers.jmx.discovery.JavaAppDiscovery;
import io.github.anycollect.readers.jmx.query.JmxQuery;
import io.github.anycollect.readers.jmx.query.JmxQueryProvider;

import javax.annotation.Nonnull;

@Extension(name = JmxReader.NAME, point = ServiceReader.class)
public class JmxReader implements ServiceReader {
    public static final String NAME = "JmxReader";
    private final PullManager puller;
    private final JavaAppDiscovery discovery;
    private final QueryProvider<JmxQuery> queries;
    private final QueryMatcherResolver matcher;

    @ExtCreator
    public JmxReader(@ExtDependency(qualifier = "puller") @Nonnull final PullManager puller,
                     @ExtDependency(qualifier = "discovery") @Nonnull final JavaAppDiscovery discovery,
                     @ExtDependency(qualifier = "queries") @Nonnull final JmxQueryProvider queries,
                     @ExtDependency(qualifier = "matcher") @Nonnull final QueryMatcherResolver matcher) {
        this.puller = puller;
        this.discovery = discovery;
        this.matcher = matcher;
        this.queries = queries;
    }

    @Override
    public void start(@Nonnull final Dispatcher dispatcher) {
        puller.start(discovery, queries, matcher, dispatcher);
    }
}
