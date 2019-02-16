package io.github.anycollect.readers.jmx.discovery;

import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.extensions.annotations.ExtPoint;
import io.github.anycollect.readers.jmx.server.JavaApp;

@ExtPoint
public interface JavaAppDiscovery extends ServiceDiscovery<JavaApp> {
}
