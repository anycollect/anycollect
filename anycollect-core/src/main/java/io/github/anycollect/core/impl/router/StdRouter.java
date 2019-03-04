package io.github.anycollect.core.impl.router;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.github.anycollect.core.api.Processor;
import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.Router;
import io.github.anycollect.core.api.Writer;
import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.impl.router.adapters.Routes;
import io.github.anycollect.core.impl.router.config.RouterConfig;
import io.github.anycollect.core.impl.router.config.TopologyItem;
import io.github.anycollect.core.impl.router.filters.FilterChain;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
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

@Extension(name = StdRouter.NAME, point = Router.class)
public final class StdRouter implements Router, Lifecycle {
    public static final String NAME = "Router";
    private static final Logger LOG = LoggerFactory.getLogger(StdRouter.class);
    private final List<Channel> channels;

    @ExtCreator
    public StdRouter(@ExtDependency(qualifier = "readers") @Nonnull final List<Reader> readers,
                     @ExtDependency(qualifier = "processors") @Nonnull final List<Processor> processors,
                     @ExtDependency(qualifier = "writers") @Nonnull final List<Writer> writers,
                     @ExtConfig @Nonnull final RouterConfig config) {
        Routes routes = Routes.create(readers, processors, writers);
        Map<MetricProducer, List<AsyncDispatcher>> topology = new HashMap<>();
        for (TopologyItem topologyItem : config.topology()) {
            MetricProducer producer = routes.getProducer(topologyItem.from());
            MetricConsumer consumer = routes.getConsumer(topologyItem.to());
            FilterChain filter = new FilterChain(topologyItem.filters());
            FilteredMetricConsumer filteredConsumer = new FilteredMetricConsumer(filter, consumer);
            List<AsyncDispatcher> destinations = topology.computeIfAbsent(producer, prod -> new ArrayList<>());
            destinations.add(makeDispatcher(filteredConsumer));
        }

        this.channels = new ArrayList<>();
        for (Map.Entry<MetricProducer, List<AsyncDispatcher>> entry : topology.entrySet()) {
            MetricProducer producer = entry.getKey();
            ForkAsyncDispatcher dispatcher = new ForkAsyncDispatcher(entry.getValue());
            Channel channel = new Channel(producer, dispatcher);
            this.channels.add(channel);
        }
        StringBuilder topologyString = new StringBuilder();
        for (Channel channel : channels) {
            topologyString.append("\n").append("\t").append(channel);
        }
        LOG.info("topology: {}", topologyString);
    }

    private static SingleAsyncDispatcher makeDispatcher(final MetricConsumer consumer) {
        ThreadFactory factory = new ThreadFactoryBuilder()
                .setNameFormat("anycollect-route(" + consumer.getAddress() + ")-[%d]")
                .build();
        ExecutorService executorService = Executors.newSingleThreadExecutor(factory);
        return new SingleAsyncDispatcher(executorService, consumer);
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
