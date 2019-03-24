package io.github.anycollect;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.anycollect.core.impl.pull.PullManagerConfig;
import io.github.anycollect.core.impl.router.config.TopologyItem;
import org.immutables.value.Value;

import java.util.Collections;
import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableCoreConfig.class)
@JsonDeserialize(as = ImmutableCoreConfig.class)
public interface CoreConfig {
    @Value.Default
    @JsonProperty("internalMonitoring")
    default InternalMonitoringConfig internalMonitoring() {
        return InternalMonitoringConfig.common(10);
    }

    @Value.Default
    @JsonProperty("pull")
    default PullManagerConfig pull() {
        return PullManagerConfig.builder().build();
    }

    @Value.Default
    @JsonProperty("export")
    default List<Export> export() {
        return Collections.emptyList();
    }

    @Value.Default
    @JsonProperty("topology")
    default List<TopologyItem> topology() {
        return Collections.emptyList();
    }
}

