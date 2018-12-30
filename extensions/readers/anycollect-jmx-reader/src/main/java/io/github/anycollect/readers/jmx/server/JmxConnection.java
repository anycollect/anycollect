package io.github.anycollect.readers.jmx.server;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import java.io.Closeable;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Objects;

public class JmxConnection implements Closeable {
    @Nullable
    private final JMXConnector connector;
    @Nonnull
    private final MBeanServerConnection connection;
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
    public final MBeanServerConnection getConnection() {
        return connection;
    }

    public final void markAsDestroyed() {
        this.destroyed = true;
    }

    public final boolean isAlive() {
        return !destroyed;
    }

    public final boolean isClosed() {
        return closed;
    }

    @Override
    public final void close() throws IOException {
        if (closed) {
            return;
        }
        if (connector != null) {
            connector.close();
        }
        closed = true;
    }
}
