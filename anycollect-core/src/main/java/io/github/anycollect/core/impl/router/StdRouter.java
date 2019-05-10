package io.github.anycollect.core.impl.router;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.github.anycollect.core.api.Processor;
import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.Router;
import io.github.anycollect.core.api.Writer;
import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.impl.router.adapters.ProcessorAdapter;
import io.github.anycollect.core.impl.router.adapters.ReaderAdapter;
import io.github.anycollect.core.impl.router.adapters.WriterAdapter;
import io.github.anycollect.core.impl.router.config.RouterConfig;
import io.github.anycollect.core.impl.router.config.TopologyItem;
import io.github.anycollect.core.api.filter.FilterChain;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static java.util.stream.Collectors.toList;

@Extension(name = StdRouter.NAME, point = Router.class)
public final class StdRouter implements Router, Lifecycle {
    public static final String NAME = "Router";
    private static final Logger LOG = LoggerFactory.getLogger(StdRouter.class);
    private final List<Channel> channels;

    @ExtCreator
    public StdRouter(@ExtDependency(qualifier = "readers") @Nonnull final List<Reader> readers,
                     @ExtDependency(qualifier = "processors") @Nonnull final List<Processor> processors,
                     @ExtDependency(qualifier = "writers") @Nonnull final List<Writer> writers,
                     @ExtDependency(qualifier = "registry") @Nonnull final MeterRegistry registry,
                     @ExtConfig @Nonnull final RouterConfig config) {
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
            producers.put(mappedProducer.getAddress(), new MonitoredMetricProducer(mappedProducer, registry));
        }
        for (MetricConsumer mappedConsumer : mappedConsumers) {
            consumers.put(mappedConsumer.getAddress(), makeBackgroundMonitoredConsumer(mappedConsumer, registry));
        }
        for (MetricProcessor mappedProcessor : mappedProcessors) {
            consumers.put(mappedProcessor.getAddress(), makeBackgroundMonitoredConsumer(mappedProcessor, registry));
            producers.put(mappedProcessor.getAddress(), new MonitoredMetricProducer(mappedProcessor, registry));
        }

        Map<MetricProducer, List<MetricConsumer>> topology = new HashMap<>();
        for (TopologyItem topologyItem : config.topology()) {
            MetricProducer producer = producers.get(topologyItem.from());
            MetricConsumer consumer = consumers.get(topologyItem.to());
            FilterChain filter = new FilterChain(topologyItem.filters());
            FilteredMetricConsumer filteredConsumer = new FilteredMetricConsumer(filter, consumer);
            List<MetricConsumer> destinations = topology.computeIfAbsent(producer, prod -> new ArrayList<>());
            destinations.add(filteredConsumer);
        }

        this.channels = new ArrayList<>();
        for (Map.Entry<MetricProducer, List<MetricConsumer>> entry : topology.entrySet()) {
            MetricProducer producer = entry.getKey();
            RouteDispatcherFanout dispatcher = new RouteDispatcherFanout(entry.getValue());
            Channel channel = new Channel(producer, dispatcher);
            this.channels.add(channel);
        }
        StringBuilder topologyString = new StringBuilder();
        for (Channel channel : channels) {
            topologyString.append("\n").append("\t").append(channel);
        }
        LOG.info("topology: {}", topologyString);
    }

    private static BackgroundMetricConsumer makeBackgroundMonitoredConsumer(final MetricConsumer consumer,
                                                                            final MeterRegistry registry) {
        ThreadFactory factory = new ThreadFactoryBuilder()
                .setNameFormat("anycollect-route(" + consumer.getAddress() + ")-[%d]")
                .build();
        ExecutorService executorService = Executors.newSingleThreadExecutor(factory);
        return new BackgroundMetricConsumer(executorService, new MonitoredMetricConsumer(consumer, registry));
    }

    @Override
    public void init() {
        LOG.info("{} has been successfully initialised", NAME);
    }

    @Override
    public void start() {
        channels.forEach(Channel::connect);
    }

    @Override
    public void destroy() {
        channels.forEach(Channel::disconnect);
        LOG.info("{} has been successfully destroyed", NAME);
    }
}
