package io.github.anycollect.core.api.target;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.common.Plugin;

import java.util.Set;

public interface ServiceDiscovery<T extends Target> extends Plugin, Lifecycle {
    Set<T> discover();
}
