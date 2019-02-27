package io.github.anycollect.core.impl.writers.socket;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.core.api.Writer;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.MetricFamily;
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

    @ExtCreator
    public SocketWriter(@ExtDependency(qualifier = "format") @Nonnull final Serializer serializer,
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
    }

    // TODO multithreading access
    @Override
    public synchronized void write(@Nonnull final List<MetricFamily> families) {
        for (MetricFamily family : families) {
            write(family);
        }
    }

    private void write(@Nonnull final MetricFamily family) {
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
}
