package io.github.anycollect.core.impl.router.adapters;

import io.github.anycollect.core.api.Processor;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.impl.router.AbstractRouterNode;
import io.github.anycollect.core.impl.router.MetricProcessor;
import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ProcessorAdapter extends AbstractRouterNode implements MetricProcessor {
    private final Processor processor;
    private final AtomicBoolean stopped = new AtomicBoolean(false);

    public ProcessorAdapter(@Nonnull final Processor processor) {
        super(processor.getId());
        this.processor = processor;
    }

    @Override
    public void consume(@Nonnull final List<? extends Sample> samples) {
        if (!stopped.get()) {
            processor.submit(samples);
        }
    }

    @Override
    public void stop() {
        stopped.set(true);
    }

    @Override
    public void start(@Nonnull final Dispatcher dispatcher) {
        processor.start(dispatcher);
    }
}
