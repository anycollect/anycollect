package io.github.anycollect.core.impl.writers.socket;

import io.github.anycollect.core.api.internal.AdaptiveSerializer;
import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.metric.Sample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.net.SocketFactory;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public final class TcpSender implements Sender {
    private static final Logger LOG = LoggerFactory.getLogger(TcpSender.class);
    private final InetSocketAddress address;
    private final SocketFactory socketFactory;
    private final AdaptiveSerializer serializer;
    private volatile Socket socket;
    private volatile OutputStream outputStream;

    public TcpSender(final String host, final int port,
                     final AdaptiveSerializer serializer) {
        this.socketFactory = SocketFactory.getDefault();
        this.address = new InetSocketAddress(host, port);
        this.serializer = serializer;
    }

    @Override
    public void connected() throws IOException {
        if (isConnected()) {
            return;
        }
        this.socket = socketFactory.createSocket(address.getAddress(), address.getPort());
        this.outputStream = new BufferedOutputStream(socket.getOutputStream());
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    @Override
    public void send(@Nonnull final Sample sample) throws SerialisationException, IOException {
        ByteBuffer buffer = serializer.serialize(sample);
        try {
            outputStream.write(buffer.array(), 0, buffer.limit());
        } finally {
            serializer.release(buffer);
        }
    }

    @Override
    public void flush() throws IOException {
        if (outputStream != null) {
            outputStream.flush();
        }
    }

    @Override
    public void closed() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            LOG.debug("unable to close output stream", e);
        } finally {
            this.outputStream = null;
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
