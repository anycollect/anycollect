package io.github.anycollect.core.impl.writers.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

public final class UdpSender implements Sender {
    private static final Logger LOG = LoggerFactory.getLogger(UdpSender.class);
    private final String host;
    private final int port;
    private InetSocketAddress address;
    private DatagramChannel channel = null;

    public UdpSender(final String host, final int port) {
        this.host = host;
        this.port = port;
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
    public void send(@Nonnull final String data) throws IOException {
        channel.send(ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8)), address);
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
