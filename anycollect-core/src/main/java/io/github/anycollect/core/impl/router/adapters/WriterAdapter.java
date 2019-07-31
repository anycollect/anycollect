package io.github.anycollect.core.impl.router.adapters;

import io.github.anycollect.core.api.Writer;
import io.github.anycollect.core.impl.router.AbstractRouterNode;
import io.github.anycollect.core.impl.router.MetricConsumer;
import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class WriterAdapter extends AbstractRouterNode implements MetricConsumer {
    private final Writer writer;
    private final AtomicBoolean stopped = new AtomicBoolean(false);

    public WriterAdapter(@Nonnull final Writer writer) {
        super(writer.getId());
        this.writer = writer;
    }

    @Override
    public void consume(@Nonnull final List<? extends Sample> samples) {
        if (!stopped.get()) {
            this.writer.write(samples);
        }
    }

    @Override
    public void stop() {
        stopped.set(true);
    }
}
