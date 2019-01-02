package io.github.anycollect.readers.jmx.processing;

import io.github.anycollect.readers.jmx.application.ApplicationRegistry;
import io.github.anycollect.readers.jmx.application.DynamicApplicationRegistry;
import io.github.anycollect.readers.jmx.discovery.DiscoverException;
import io.github.anycollect.readers.jmx.discovery.ServerDiscovery;
import io.github.anycollect.readers.jmx.module.QueryModule;
import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Set;

public final class QuerySchedulerUpdateJob implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(QuerySchedulerUpdateJob.class);
    private final QueryScheduler scheduler;
    private final DynamicApplicationRegistry registry;
    private final ServerDiscovery discovery;
    private final QueryModule module;

    public QuerySchedulerUpdateJob(@Nonnull final QueryScheduler scheduler,
                                   @Nonnull final DynamicApplicationRegistry registry,
                                   @Nonnull final ServerDiscovery discovery,
                                   @Nonnull final QueryModule module) {
        this.scheduler = scheduler;
        this.registry = registry;
        this.discovery = discovery;
        this.module = module;
    }

    @Override
    public void run() {
        try {
            ApplicationRegistry applicationRegistry = registry.getCurrentSnapshot();
            Set<Server> servers = discovery.getServers(applicationRegistry);
            Set<Query> queries = module.getQueries();
            scheduler.schedule(servers, queries);
        } catch (DiscoverException e) {
            LOG.debug("unable to discover servers, scheduled server list will not be updated", e);
        } catch (Exception e) {
            LOG.debug("unexpected exception, scheduler will not be updated", e);
        }
    }
}
