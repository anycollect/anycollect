package io.github.anycollect.core.impl.router.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.anycollect.core.api.filter.Filter;
import io.github.anycollect.core.impl.filters.AcceptAllFilter;
import org.immutables.value.Value;

import java.util.Collections;
import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableTopologyItem.class)
@JsonDeserialize(as = ImmutableTopologyItem.class)
public interface TopologyItem {
    static TopologyItem of(String from, String to) {
        return ImmutableTopologyItem.builder().from(from).to(to).build();
    }

    String from();

    String to();

    @Value.Default
    default List<Filter> filters() {
        return Collections.singletonList(new AcceptAllFilter());
    }
}
