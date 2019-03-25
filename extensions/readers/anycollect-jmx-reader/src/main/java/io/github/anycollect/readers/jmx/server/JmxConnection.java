package io.github.anycollect.readers.jmx.server;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import java.io.Closeable;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class JmxConnection implements Closeable {
    @Nullable
    private final JMXConnector connector;
    @Nonnull
    private final MBeanServerConnection connection;
    private final CopyOnWriteArrayList<JmxEventListener> listeners = new CopyOnWriteArrayList<>();
    private volatile boolean destroyed = false;
    private volatile boolean closed = false;

    public JmxConnection(@Nullable final JMXConnector connector, @Nonnull final MBeanServerConnection connection) {
        Objects.requireNonNull(connection, "connection must not be null");
        this.connector = connector;
        this.connection = connection;
    }

    public static JmxConnection local() {
        return new JmxConnection(null, ManagementFactory.getPlatformMBeanServer());
    }

    @Nonnull
    public MBeanServerConnection getConnection() {
        return connection;
    }

    public void markAsDestroyed() {
        this.destroyed = true;
    }

    public boolean isAlive() {
        return !destroyed;
    }

    public boolean isClosed() {
        return closed;
    }

    public void addListener(@Nonnull final JmxEventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }
        if (connector != null) {
            connector.close();
        }
        closed = true;
        JmxEvent event = new JmxConnectionClosedEvent();
        for (JmxEventListener listener : listeners) {
            listener.handle(event);
        }
    }
}
