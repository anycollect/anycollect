package io.github.anycollect.core.api.target;

import java.util.Collections;
import java.util.Set;

public interface SelfDiscovery extends ServiceDiscovery<SelfTarget> {
    SelfTarget self();

    @Override
    default Set<SelfTarget> discover() {
        return Collections.singleton(self());
    }
}
