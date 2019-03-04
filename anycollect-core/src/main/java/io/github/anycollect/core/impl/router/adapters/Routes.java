package io.github.anycollect.core.impl.router.adapters;

import io.github.anycollect.core.api.Processor;
import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.Writer;
import io.github.anycollect.core.impl.router.MetricConsumer;
import io.github.anycollect.core.impl.router.MetricProcessor;
import io.github.anycollect.core.impl.router.MetricProducer;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public final class Routes {
    private final Map<String, MetricProducer> producers;
    private final Map<String, MetricConsumer> consumers;

    public static Routes create(@Nonnull final List<Reader> readers,
                         @Nonnull final List<Processor> processors,
                         @Nonnull final List<Writer> writers) {
        List<MetricProducer> mappedProducers = readers.stream()
                .map(ReaderAdapter::new)
                .collect(toList());
        List<MetricProcessor> mappedProcessors = processors.stream()
                .map(ProcessorAdapter::new)
                .collect(toList());
        List<MetricConsumer> mappedConsumers = writers.stream()
                .map(WriterAdapter::new)
                .collect(toList());

        Map<String, MetricProducer> producers = new HashMap<>();
        Map<String, MetricConsumer> consumers = new HashMap<>();
        for (MetricProducer mappedProducer : mappedProducers) {
            producers.put(mappedProducer.getAddress(), mappedProducer);
        }
        for (MetricConsumer mappedConsumer : mappedConsumers) {
            consumers.put(mappedConsumer.getAddress(), mappedConsumer);
        }
        for (MetricProcessor mappedProcessor : mappedProcessors) {
            consumers.put(mappedProcessor.getAddress(), mappedProcessor);
            producers.put(mappedProcessor.getAddress(), mappedProcessor);
        }
        return new Routes(producers, consumers);
    }

    private Routes(final Map<String, MetricProducer> producers, final Map<String, MetricConsumer> consumers) {
        this.producers = producers;
        this.consumers = consumers;
    }

    public MetricProducer getProducer(@Nonnull final String name) {
        return producers.get(name);
    }

    public MetricConsumer getConsumer(@Nonnull final String name) {
        return consumers.get(name);
    }
}
