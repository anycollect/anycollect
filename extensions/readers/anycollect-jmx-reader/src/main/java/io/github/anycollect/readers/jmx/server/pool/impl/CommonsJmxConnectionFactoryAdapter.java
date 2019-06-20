package io.github.anycollect.readers.jmx.server.pool.impl;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.readers.jmx.server.JmxConnection;
import io.github.anycollect.readers.jmx.server.JmxConnectionFactory;
import io.github.anycollect.readers.jmx.server.pool.AsyncConnectionCloser;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class CommonsJmxConnectionFactoryAdapter implements PooledObjectFactory<JmxConnection> {
    private final AsyncConnectionCloser closer;
    private final JmxConnectionFactory jmxConnectionFactory;

    public CommonsJmxConnectionFactoryAdapter(@Nonnull final JmxConnectionFactory jmxConnectionFactory,
                                              @Nonnull final AsyncConnectionCloser closer) {
        Objects.requireNonNull(jmxConnectionFactory, "jmx connection factory must not be null");
        Objects.requireNonNull(closer, "closer must not be null");
        this.jmxConnectionFactory = jmxConnectionFactory;
        this.closer = closer;
    }

    @Override
    public PooledObject<JmxConnection> makeObject() throws ConnectionException {
        JmxConnection jmxConnection = jmxConnectionFactory.createJmxConnection();
        DefaultPooledObject<JmxConnection> object = new DefaultPooledObject<>(jmxConnection);
        jmxConnection.onConnectionDrop(object::invalidate);
        return object;
    }

    @Override
    public void destroyObject(final PooledObject<JmxConnection> p) {
        JmxConnection jmxConnection = p.getObject();
        jmxConnection.markAsDestroyed();
        closer.closeAsync(jmxConnection);
    }

    @Override
    public boolean validateObject(final PooledObject<JmxConnection> p) {
        return p.getObject().isAlive();
    }

    @Override
    public void activateObject(final PooledObject<JmxConnection> p) {
    }

    @Override
    public void passivateObject(final PooledObject<JmxConnection> p) {
    }
}
