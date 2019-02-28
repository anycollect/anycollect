package io.github.anycollect.core.impl.writers.socket;

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
    private volatile Socket socket;
    private volatile Writer writer;

    public TcpSender(final String host, final int port) {
        socketFactory = SocketFactory.getDefault();
        address = new InetSocketAddress(host, port);
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
    public void send(@Nonnull final String data) throws IOException {
        writer.write(data);
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
