package io.github.anycollect.core.impl.router.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableRouterConfig.class)
@JsonDeserialize(as = ImmutableRouterConfig.class)
public interface RouterConfig {
    List<TopologyItem> topology();
}
