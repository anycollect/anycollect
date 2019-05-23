package io.github.anycollect.core.impl.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Channel {
    private static final Logger LOG = LoggerFactory.getLogger(Channel.class);
    private final MetricProducer producer;
    private final RouteDispatcher consumer;

    public Channel(@Nonnull final MetricProducer producer,
                   @Nonnull final RouteDispatcher consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    public void connect() {
        LOG.info("Connecting channel: {}->{}", producer.getAddress(), consumer);
        producer.start(consumer);
    }

    public void disconnect() {
        LOG.info("Disconnecting channel: {}->{}", producer.getAddress(), consumer);
        consumer.stop();
    }

    @Override
    public String toString() {
        return producer + "->" + consumer.toString();
    }
}
