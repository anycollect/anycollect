package io.github.anycollect.core.impl.writers.socket;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.core.api.Writer;
import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.internal.AdaptiveSerializer;
import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.extensions.annotations.*;
import io.github.anycollect.metric.Sample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.util.List;

@NotThreadSafe
@Extension(name = SocketWriter.NAME, contracts = Writer.class)
public final class SocketWriter implements Writer, Lifecycle {
    public static final String NAME = "SocketWriter";
    private static final Logger LOG = LoggerFactory.getLogger(SocketWriter.class);
    private final Sender sender;
    private final String id;

    @ExtCreator
    public SocketWriter(@ExtDependency(qualifier = "format") @Nonnull final Serializer serializer,
                        @InstanceId @Nonnull final String id,
                        @ExtConfig @Nonnull final SocketConfig config) {
        AdaptiveSerializer adaptiveSerializer = AdaptiveSerializer.wrap(serializer);
        if (config.getProtocol() == Protocol.TCP) {
            this.sender = new TcpSender(config.getHost(), config.getPort(), adaptiveSerializer);
        } else if (config.getProtocol() == Protocol.UDP) {
            this.sender = new UdpSender(config.getHost(), config.getPort(), adaptiveSerializer);
        } else {
            LOG.error("protocol {} is not supported", config.getProtocol());
            throw new ConfigurationException("protocol " + config.getProtocol() + " is not supported");
        }
        this.id = id;
    }

    @Override
    public void write(@Nonnull final List<? extends Sample> metrics) {
        for (Sample sample : metrics) {
            write(sample);
        }
    }

    private void write(@Nonnull final Sample sample) {
        try {
            sender.connected();
            sender.send(sample);
            // TODO schedule flush
            sender.flush();
        } catch (SerialisationException e) {
            LOG.debug("could not serialize metric {}", sample, e);
        } catch (IOException e) {
            LOG.trace("fail to send metric: {}", sample, e);
            sender.closed();
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void init() {
        LOG.info("{} has been successfully initialised", NAME);
    }

    @Override
    public void destroy() {
        LOG.info("{}({}) has been successfully destroyed", id, NAME);
    }
}
