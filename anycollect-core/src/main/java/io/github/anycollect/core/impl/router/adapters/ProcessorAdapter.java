package io.github.anycollect.core.impl.router.adapters;

import io.github.anycollect.core.api.Processor;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.impl.router.AbstractRouterNode;
import io.github.anycollect.core.impl.router.MetricProcessor;
import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import java.util.List;

public final class ProcessorAdapter extends AbstractRouterNode implements MetricProcessor {
    private final Processor processor;

    public ProcessorAdapter(@Nonnull final Processor processor) {
        super(processor.getId());
        this.processor = processor;
    }

    @Override
    public void consume(@Nonnull final List<MetricFamily> families) {
        processor.submit(families);
    }

    @Override
    public void start(@Nonnull final Dispatcher dispatcher) {
        processor.start(dispatcher);
    }
}
