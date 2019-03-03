package io.github.anycollect.core.impl.router.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nonnull;

@Value.Immutable
@Value.Style(passAnnotations = Nonnull.class)
@JsonSerialize(as = ImmutableTopologyItem.class)
@JsonDeserialize(as = ImmutableTopologyItem.class)
public interface TopologyItem {
    @Nonnull
    String from();

    @Nonnull
    String to();
}
