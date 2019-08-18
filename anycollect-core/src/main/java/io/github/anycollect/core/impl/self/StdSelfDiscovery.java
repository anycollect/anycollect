package io.github.anycollect.core.impl.self;

import io.github.anycollect.core.api.target.SelfDiscovery;
import io.github.anycollect.core.api.target.SelfTarget;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;

import javax.annotation.Nullable;

@Extension(name = StdSelfDiscovery.NAME, contracts = SelfDiscovery.class)
public class StdSelfDiscovery implements SelfDiscovery {
    public static final String NAME = "SelfDiscovery";
    private final SelfTarget self;

    @ExtCreator
    public StdSelfDiscovery(@ExtConfig(optional = true) @Nullable final SelfDiscoveryConfig optConfig) {
        SelfDiscoveryConfig config = optConfig != null ? optConfig : SelfDiscoveryConfig.DEFAULT;
        this.self = new SelfTarget(config.targetId());
    }

    @Override
    public SelfTarget self() {
        return self;
    }
}
