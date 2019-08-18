package io.github.anycollect.readers.process;

import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Cancellation;
import io.github.anycollect.core.api.internal.PullManager;
import io.github.anycollect.core.api.internal.QueryMatcher;
import io.github.anycollect.core.api.internal.QueryMatcherResolver;
import io.github.anycollect.core.api.query.QueryProvider;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.extensions.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

import javax.annotation.Nonnull;

@Extension(name = ProcessReader.NAME, contracts = Reader.class)
public final class ProcessReader implements Reader, Lifecycle {
    public static final String NAME = "ProcessReader";
    private static final Logger LOG = LoggerFactory.getLogger(ProcessReader.class);
    private final PullManager pullManager;
    private final ServiceDiscovery<Process> discovery;
    private final ProcessReaderConfig config;
    private final String id;
    private volatile Cancellation cancellation;

    @ExtCreator
    public ProcessReader(@ExtDependency(qualifier = "puller") @Nonnull final PullManager pullManager,
                         @ExtDependency(qualifier = "discovery") @Nonnull final ServiceDiscovery<Process> discovery,
                         @ExtConfig @Nonnull final ProcessReaderConfig config,
                         @InstanceId @Nonnull final String id) {
        this.pullManager = pullManager;
        this.discovery = discovery;
        this.config = config;
        this.id = id;
    }

    @Override
    public void start(@Nonnull final Dispatcher dispatcher) {
        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        ProcessQuery query = new ProcessQuery(config.prefix(), config.cpuUsageKey(), config.memoryUsageKey(), memory);
        this.cancellation = pullManager.start(id, discovery, QueryProvider.singleton(query),
                QueryMatcherResolver.consistent(QueryMatcher.all(config.period())), dispatcher);
    }

    @Override
    public void stop() {
        if (cancellation != null) {
            cancellation.cancel();
        }
        LOG.info("{}({}) has been successfully stopped", id, NAME);
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
