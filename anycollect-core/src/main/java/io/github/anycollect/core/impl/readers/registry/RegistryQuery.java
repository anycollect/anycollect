package io.github.anycollect.core.impl.readers.registry;

import io.github.anycollect.core.api.query.SelfQuery;
import io.github.anycollect.core.api.target.SelfTarget;
import io.github.anycollect.metric.MeterId;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

public class RegistryQuery extends SelfQuery {
    private final MeterRegistry registry;
    private final Predicate<MeterId> filter;

    public RegistryQuery(@Nonnull final MeterRegistry registry,
                         @Nonnull final Predicate<MeterId> filter) {
        super("meter.registry");
        this.registry = registry;
        this.filter = filter;
    }

    @Override
    public List<Metric> executeOn(@Nonnull final SelfTarget target) {
        // TODO add target tags
        return registry.measure(filter);
    }
}
