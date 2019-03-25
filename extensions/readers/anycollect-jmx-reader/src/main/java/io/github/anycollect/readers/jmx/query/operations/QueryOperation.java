package io.github.anycollect.readers.jmx.query.operations;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.readers.jmx.server.JmxConnection;

import javax.annotation.Nonnull;
import javax.management.MBeanServerConnection;

public interface QueryOperation<T> {
    default T operate(@Nonnull JmxConnection connection) throws QueryException, ConnectionException {
        return operate(connection.getConnection());
    }

    T operate(@Nonnull MBeanServerConnection connection) throws QueryException, ConnectionException;
}
