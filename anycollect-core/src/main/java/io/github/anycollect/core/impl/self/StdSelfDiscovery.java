package io.github.anycollect.core.impl.self;

import io.github.anycollect.core.api.target.SelfDiscovery;
import io.github.anycollect.core.api.target.SelfTarget;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;

import javax.annotation.Nonnull;

@Extension(name = StdSelfDiscovery.NAME, point = SelfDiscovery.class)
public class StdSelfDiscovery implements SelfDiscovery {
    public static final String NAME = "SelfDiscovery";
    private final SelfTarget self;

    @ExtCreator
    public StdSelfDiscovery(@ExtConfig @Nonnull final SelfDiscoveryConfig config) {
        this.self = new SelfTarget(config.targetId());
    }

    @Override
    public SelfTarget self() {
        return self;
    }
}
