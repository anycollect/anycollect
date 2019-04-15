package io.github.anycollect.core.impl.writers.socket;

import io.github.anycollect.core.api.serialization.RoundRobinSerializer;
import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.metric.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.net.SocketFactory;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class TcpSender implements Sender {
    private static final Logger LOG = LoggerFactory.getLogger(TcpSender.class);
    private final InetSocketAddress address;
    private final SocketFactory socketFactory;
    private final Charset charset = StandardCharsets.UTF_8;
    private final RoundRobinSerializer<StringBuilder> serializer;
    private final StringBuilder builder;
    private char[] carrier;
    private volatile Socket socket;
    private volatile Writer writer;

    public TcpSender(final String host, final int port,
                     final RoundRobinSerializer<StringBuilder> serializer) {
        this.socketFactory = SocketFactory.getDefault();
        this.address = new InetSocketAddress(host, port);
        this.serializer = serializer;
        this.builder = new StringBuilder();
        this.carrier = new char[1024];
    }

    @Override
    public void connected() throws IOException {
        if (isConnected()) {
            return;
        }
        this.socket = socketFactory.createSocket(address.getAddress(), address.getPort());
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), charset));
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    @Override
    public void send(@Nonnull final Metric metric) throws SerialisationException, IOException {
        serializer.serialize(metric, builder);
        if (carrier.length < builder.length()) {
            carrier = new char[builder.length()];
        }
        builder.getChars(0, builder.length(), carrier, 0);
        writer.write(carrier, 0, builder.length());
    }

    @Override
    public void flush() throws IOException {
        if (writer != null) {
            writer.flush();
        }
    }

    @Override
    public void closed() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            LOG.debug("unable to close writer", e);
        } finally {
            this.writer = null;
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            LOG.debug("unable to close socket", e);
        } finally {
            this.socket = null;
        }
    }
}
