package io.github.anycollect.readers.jmx.query.operations;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.readers.jmx.server.JmxConnection;
import io.github.anycollect.readers.jmx.server.JmxConnectionDropListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import java.io.IOException;
import java.util.Collection;

public final class SubscribeOperation implements QueryOperation<Subscription>, JmxConnectionDropListener {
    private static final Logger LOG = LoggerFactory.getLogger(SubscribeOperation.class);
    private final Collection<ObjectName> objectNames;
    private final NotificationListener listener;
    private volatile Subscription subscription;

    public SubscribeOperation(@Nonnull final Collection<ObjectName> objectNames,
                              @Nonnull final NotificationListener listener) {
        this.objectNames = objectNames;
        this.listener = listener;
    }

    @Override
    public Subscription operate(@Nonnull final JmxConnection connection) throws ConnectionException {
        connection.onConnectionDrop(this);
        return operate(connection.getConnection());
    }

    @Override
    public Subscription operate(@Nonnull final MBeanServerConnection connection)
            throws ConnectionException {
        for (ObjectName objectName : objectNames) {
            try {
                connection.addNotificationListener(objectName, listener, null, null);
            } catch (InstanceNotFoundException e) {
                LOG.warn("instance not found", e);
            } catch (IOException e) {
                throw new ConnectionException("could not subscribe", e);
            }
        }
        Subscription subscription = new Subscription();
        this.subscription = subscription;
        return subscription;
    }

    @Override
    public void onDrop() {
        if (subscription != null) {
            subscription.invalidate();
        }
    }
}
