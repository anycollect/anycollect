package io.github.anycollect.readers.jmx.server;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnectionNotification;
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
    @SuppressWarnings("FieldCanBeLocal")
    private final ConnectionListener connectionListener = new ConnectionListener();
    private final CopyOnWriteArrayList<JmxConnectionDropListener> listeners = new CopyOnWriteArrayList<>();
    private volatile boolean destroyed = false;
    private volatile boolean closed = false;

    public JmxConnection(@Nullable final JMXConnector connector, @Nonnull final MBeanServerConnection connection) {
        Objects.requireNonNull(connection, "connection must not be null");
        this.connector = connector;
        this.connection = connection;
        if (connector != null) {
            this.connector.addConnectionNotificationListener(connectionListener, null, null);
        }
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

    public void onConnectionDrop(@Nonnull final JmxConnectionDropListener listener) {
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
    }

    private class ConnectionListener implements NotificationListener {
        @Override
        public void handleNotification(final Notification notification, final Object handback) {
            if (notification instanceof JMXConnectionNotification) {
                JMXConnectionNotification connectionNotification = (JMXConnectionNotification) notification;
                if (JMXConnectionNotification.CLOSED.equals(connectionNotification.getType())
                        || JMXConnectionNotification.FAILED.equals(connectionNotification.getType())) {
                    for (JmxConnectionDropListener listener : listeners) {
                        listener.onDrop();
                    }
                }
            }
        }
    }
}
