package io.github.anycollect.core.impl.writers.socket;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.core.api.Writer;
import io.github.anycollect.extensions.annotations.*;
import io.github.anycollect.metric.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

@Extension(name = SocketWriter.NAME, point = Writer.class)
public final class SocketWriter implements Writer, Lifecycle {
    public static final String NAME = "SocketWriter";
    private static final Logger LOG = LoggerFactory.getLogger(SocketWriter.class);
    private final Serializer serializer;
    private final Sender sender;
    private final String id;

    @ExtCreator
    public SocketWriter(@ExtDependency(qualifier = "format") @Nonnull final Serializer serializer,
                        @InstanceId @Nonnull final String id,
                        @ExtConfig @Nonnull final SocketConfig config) {
        this.serializer = serializer;
        if (config.getProtocol() == Protocol.TCP) {
            this.sender = new TcpSender(config.getHost(), config.getPort());
        } else if (config.getProtocol() == Protocol.UDP) {
            this.sender = new UdpSender(config.getHost(), config.getPort());
        } else {
            LOG.error("protocol {} is not supported", config.getProtocol());
            // TODO acceptable exception
            throw new RuntimeException("protocol " + config.getProtocol() + " is not supported");
        }
        this.id = id;
    }

    // TODO multithreading access
    @Override
    public synchronized void write(@Nonnull final List<Metric> families) {
        for (Metric family : families) {
            write(family);
        }
    }

    private void write(@Nonnull final Metric family) {
        String data = serializer.serialize(family);
        try {
            sender.connected();
            sender.send(data);
            // TODO schedule flush
            sender.flush();
        } catch (IOException e) {
            LOG.debug("fail to send metric family: {}", family);
            sender.closed();
        }
    }

    @Override
    public String getId() {
        return id;
    }
}
