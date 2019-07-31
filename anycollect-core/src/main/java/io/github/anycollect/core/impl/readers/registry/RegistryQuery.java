package io.github.anycollect.core.impl.readers.registry;

import io.github.anycollect.core.api.query.SelfQuery;
import io.github.anycollect.metric.MeterId;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

public final class RegistryQuery extends SelfQuery {
    private final MeterRegistry registry;
    private final Predicate<MeterId> filter;

    public RegistryQuery(@Nonnull final MeterRegistry registry,
                         @Nonnull final Predicate<MeterId> filter) {
        super("meter.registry");
        this.registry = registry;
        this.filter = filter;
    }

    @Override
    public List<Sample> execute() {
        return registry.measure(filter);
    }
}
