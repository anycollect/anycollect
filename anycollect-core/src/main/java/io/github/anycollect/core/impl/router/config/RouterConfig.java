package io.github.anycollect.core.impl.router.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nonnull;
import java.util.List;

@Value.Immutable
@Value.Style(passAnnotations = Nonnull.class)
@JsonSerialize(as = ImmutableRouterConfig.class)
@JsonDeserialize(as = ImmutableRouterConfig.class)
public interface RouterConfig {
    @Nonnull
    List<TopologyItem> topology();
}
