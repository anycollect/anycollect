package io.github.anycollect.readers.jmx.query.operations;

import io.github.anycollect.core.exceptions.ConnectionException;

import javax.annotation.Nonnull;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.io.IOException;
import java.util.Set;

public final class QueryObjectNames implements QueryOperation<Set<ObjectName>> {
    private final ObjectName objectPattern;

    public QueryObjectNames(@Nonnull final ObjectName objectPattern) {
        this.objectPattern = objectPattern;
    }

    @Override
    public Set<ObjectName> operate(@Nonnull final MBeanServerConnection connection) throws ConnectionException {
        try {
            return connection.queryNames(objectPattern, null);
        } catch (IOException e) {
            throw new ConnectionException("could not query names", e);
        }
    }
}
