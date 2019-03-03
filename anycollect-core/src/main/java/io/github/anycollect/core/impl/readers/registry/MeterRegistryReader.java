package io.github.anycollect.core.impl.readers.registry;

import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.PullManager;
import io.github.anycollect.core.api.internal.QueryMatcher;
import io.github.anycollect.core.api.internal.QueryMatcherResolver;
import io.github.anycollect.core.api.query.QueryProvider;
import io.github.anycollect.core.api.target.SelfDiscovery;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.annotations.InstanceId;
import io.github.anycollect.metric.MeterRegistry;

import javax.annotation.Nonnull;

// TODO configure different pull period for different meters using filters
@Extension(name = MeterRegistryReader.NAME, point = Reader.class)
public class MeterRegistryReader implements Reader {
    public static final String NAME = "MeterRegistryReader";
    private final PullManager pullManager;
    private final SelfDiscovery selfDiscovery;
    private final MeterRegistry registry;
    private final String id;

    @ExtCreator
    public MeterRegistryReader(@ExtDependency(qualifier = "puller") @Nonnull final PullManager pullManager,
                               @ExtDependency(qualifier = "self") @Nonnull final SelfDiscovery selfDiscovery,
                               @ExtDependency(qualifier = "registry") @Nonnull final MeterRegistry registry,
                               @InstanceId @Nonnull final String id) {
        this.pullManager = pullManager;
        this.selfDiscovery = selfDiscovery;
        this.registry = registry;
        this.id = id;
    }

    @Override
    public void start(@Nonnull final Dispatcher dispatcher) {
        QueryProvider<RegistryQuery> singleton = QueryProvider.singleton(new RegistryQuery(registry, meterId -> true));
        pullManager.start(selfDiscovery, singleton, QueryMatcherResolver.consistent(QueryMatcher.all()), dispatcher);
    }

    @Override
    public String getId() {
        return id;
    }
}
