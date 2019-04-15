package io.github.anycollect.core.impl.writers.socket;

import io.github.anycollect.core.api.serialization.RoundRobinSerializer;
import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.metric.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public final class UdpSender implements Sender {
    private static final Logger LOG = LoggerFactory.getLogger(UdpSender.class);
    private final String host;
    private final int port;
    private final RoundRobinSerializer<ByteBuffer> serializer;
    private final ByteBuffer carrier;
    private InetSocketAddress address;
    private DatagramChannel channel = null;

    public UdpSender(final String host, final int port, final RoundRobinSerializer<ByteBuffer> serializer) {
        this.host = host;
        this.port = port;
        this.serializer = serializer;
        this.carrier = ByteBuffer.allocate(2048);
        address = null;
    }

    @Override
    public void connected() throws IOException {
        if (isConnected()) {
            return;
        }
        address = new InetSocketAddress(host, port);
        channel = DatagramChannel.open();
    }

    @Override
    public boolean isConnected() {
        return channel != null && !channel.socket().isClosed();
    }

    @Override
    public void send(@Nonnull final Metric metric) throws SerialisationException, IOException {
        serializer.serialize(metric, carrier);
        channel.send(carrier, address);
    }

    @Override
    public void flush() {
    }

    @Override
    public void closed() {
        if (isConnected()) {
            return;
        }
        if (channel != null) {
            try {
                try {
                    channel.close();
                } catch (IOException e) {
                    LOG.debug("unable to close udp socket, address: {}", address, e);
                }
            } finally {
                channel = null;
            }
        }
    }
}
