package io.github.anycollect.readers.jmx.discovery;

import io.github.anycollect.readers.jmx.application.ApplicationRegistry;
import io.github.anycollect.readers.jmx.server.Server;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Set;

@ThreadSafe
public interface ServerDiscovery {
    @Nonnull
    Set<Server> getServers(@Nonnull ApplicationRegistry registry) throws DiscoverException;
}
