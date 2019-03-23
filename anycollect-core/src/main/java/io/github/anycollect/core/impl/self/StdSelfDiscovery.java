package io.github.anycollect.core.impl.self;

import io.github.anycollect.core.api.target.SelfDiscovery;
import io.github.anycollect.core.api.target.SelfTarget;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;

@Extension(name = StdSelfDiscovery.NAME, point = SelfDiscovery.class)
public class StdSelfDiscovery implements SelfDiscovery {
    public static final String NAME = "SelfDiscovery";
    private final SelfTarget self;

    @ExtCreator
    public StdSelfDiscovery() {
        this.self = new SelfTarget("self");
    }

    @Override
    public SelfTarget self() {
        return self;
    }
}
