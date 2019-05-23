package io.github.anycollect.core.impl.readers.registry;

import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.PullManager;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.annotations.InstanceId;
import io.github.anycollect.metric.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

// TODO configure different pull period for different meters using filters
@Extension(name = MeterRegistryReader.NAME, point = Reader.class)
public final class MeterRegistryReader implements Reader, Lifecycle {
    public static final String NAME = "MeterRegistryReader";
    private static final Logger LOG = LoggerFactory.getLogger(MeterRegistryReader.class);
    private final PullManager pullManager;
    private final MeterRegistry registry;
    private final String id;

    @ExtCreator
    public MeterRegistryReader(@ExtDependency(qualifier = "puller") @Nonnull final PullManager pullManager,
                               @ExtDependency(qualifier = "registry") @Nonnull final MeterRegistry registry,
                               @InstanceId @Nonnull final String id) {
        this.pullManager = pullManager;
        this.registry = registry;
        this.id = id;
    }

    @Override
    public void start(@Nonnull final Dispatcher dispatcher) {
        pullManager.start(new RegistryQuery(registry, meterId -> true), dispatcher);
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
