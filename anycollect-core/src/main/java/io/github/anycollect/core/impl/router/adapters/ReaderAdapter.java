package io.github.anycollect.core.impl.router.adapters;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.impl.router.MetricProducer;

import javax.annotation.Nonnull;

public class ReaderAdapter implements MetricProducer {
    private final Reader reader;

    public ReaderAdapter(@Nonnull final Reader reader) {
        this.reader = reader;
    }

    @Override
    public void start(@Nonnull final Dispatcher dispatcher) {
        reader.start(dispatcher);
    }
}
