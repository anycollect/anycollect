package io.github.anycollect.core.impl.router;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Channel {
    private final MetricProducer producer;
    private final AsyncDispatcher consumer;

    public Channel(@Nonnull final MetricProducer producer,
                   @Nonnull final AsyncDispatcher consumer) {
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
