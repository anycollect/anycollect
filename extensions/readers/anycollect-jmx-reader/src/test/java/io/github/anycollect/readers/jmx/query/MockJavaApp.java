package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.core.api.target.AbstractTarget;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.jmx.query.operations.QueryOperation;
import io.github.anycollect.readers.jmx.server.JavaApp;

import javax.annotation.Nonnull;
import javax.management.MBeanServerConnection;

public class MockJavaApp extends AbstractTarget implements JavaApp {
    private final MBeanServerConnection connection;

    protected MockJavaApp() {
        this(null);
    }

    protected MockJavaApp(MBeanServerConnection connection) {
        this(connection, Tags.empty());
    }

    public MockJavaApp(MBeanServerConnection connection, @Nonnull Tags tags) {
        super("simple", tags, Tags.empty());
        this.connection = connection;
    }

    @Override
    public <T> T operate(@Nonnull QueryOperation<T> operation) throws QueryException, ConnectionException {
        return operation.operate(connection);
    }
}
