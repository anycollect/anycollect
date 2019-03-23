package io.github.anycollect.readers.jmx.query.operations;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;

import javax.annotation.Nonnull;
import javax.management.MBeanServerConnection;

public interface QueryOperation<T> {
    T operate(@Nonnull MBeanServerConnection connection) throws QueryException, ConnectionException;
}
