package io.github.anycollect.readers.jmx.query.operations;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;

import javax.annotation.Nonnull;
import javax.management.*;
import java.io.IOException;
import java.util.List;

public final class QueryAttributes implements QueryOperation<List<Attribute>> {
    private final ObjectName objectName;
    private final String[] attributeNames;

    public QueryAttributes(@Nonnull final ObjectName objectName, @Nonnull final String[] attributeNames) {
        this.objectName = objectName;
        this.attributeNames = attributeNames;
    }

    @Override
    public List<Attribute> operate(@Nonnull final MBeanServerConnection connection)
            throws QueryException, ConnectionException {
        try {
            return connection.getAttributes(objectName, attributeNames).asList();
        } catch (IOException e) {
            throw new ConnectionException("could not get attributes", e);
        } catch (InstanceNotFoundException | ReflectionException e) {
            throw new QueryException("could not get attributes", e);
        }
    }
}
