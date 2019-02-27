package io.github.anycollect.core.impl.router.adapters;

import io.github.anycollect.core.api.Writer;
import io.github.anycollect.core.impl.router.MetricConsumer;
import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import java.util.List;

public class WriterAdapter implements MetricConsumer {
    private final Writer writer;

    public WriterAdapter(@Nonnull final Writer writer) {
        this.writer = writer;
    }

    @Override
    public void consume(@Nonnull final List<MetricFamily> families) {
        this.writer.write(families);
    }
}
