package io.github.anycollect.readers.jmx.discovery;

import io.github.anycollect.readers.jmx.application.ApplicationRegistry;
import io.github.anycollect.readers.jmx.server.Server;

import javax.annotation.Nonnull;
import java.util.List;

public interface ServerDiscovery {
    @Nonnull
    List<Server> getServers(@Nonnull ApplicationRegistry registry) throws DiscoverException;
}
