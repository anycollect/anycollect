package io.github.anycollect.readers.jmx.server;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;

public final class JmxConnection implements Closeable {
    @Nullable
    private final JMXConnector connector;
    @Nonnull
    private final MBeanServerConnection connection;
    private volatile boolean destroyed = false;

    public JmxConnection(@Nullable final JMXConnector connector, @Nonnull final MBeanServerConnection connection) {
        Objects.requireNonNull(connection, "connection must not be null");
        this.connector = connector;
        this.connection = connection;
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

    @Override
    public void close() throws IOException {
        if (connector != null) {
            connector.close();
        }
    }
}
