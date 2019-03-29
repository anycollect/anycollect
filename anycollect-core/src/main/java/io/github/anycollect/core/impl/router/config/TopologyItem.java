package io.github.anycollect.core.impl.router.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.anycollect.core.impl.filters.AcceptAllFilter;
import io.github.anycollect.core.impl.filters.Filter;
import org.immutables.value.Value;

import java.util.Collections;
import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableTopologyItem.class)
@JsonDeserialize(as = ImmutableTopologyItem.class)
public interface TopologyItem {
    String from();

    String to();

    @Value.Default
    default List<Filter> filters() {
        return Collections.singletonList(new AcceptAllFilter());
    }
}
