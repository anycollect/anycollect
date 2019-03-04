package io.github.anycollect.core.impl.router;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Channel {
    private final MetricProducer producer;
    private final RouteDispatcher consumer;

    public Channel(@Nonnull final MetricProducer producer,
                   @Nonnull final RouteDispatcher consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    public void connect() {
        producer.start(consumer);
    }

    public void disconnect() {
        consumer.stop();
    }

    @Override
    public String toString() {
        return producer + "->" + consumer.toString();
    }
}
