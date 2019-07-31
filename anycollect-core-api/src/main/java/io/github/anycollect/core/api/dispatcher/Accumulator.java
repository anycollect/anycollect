package io.github.anycollect.core.api.dispatcher;

import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Accumulator implements Dispatcher {
    private final Queue<Sample> samples;

    public Accumulator() {
        samples = new ConcurrentLinkedQueue<>();
    }

    public List<Sample> purge() {
        return new ArrayList<>(samples);
    }

    @Override
    public void dispatch(@Nonnull final Sample sample) {
        this.samples.add(sample);
    }

    @Override
    public void dispatch(@Nonnull final List<Sample> samples) {
        this.samples.addAll(samples);
    }
}
