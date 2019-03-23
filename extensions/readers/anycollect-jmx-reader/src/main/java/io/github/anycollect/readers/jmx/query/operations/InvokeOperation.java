package io.github.anycollect.readers.jmx.query.operations;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;

import javax.annotation.Nonnull;
import javax.management.*;
import java.io.IOException;

public final class InvokeOperation implements QueryOperation<Object> {
    private final ObjectName objectName;
    private final String operationName;
    private final Object[] params;
    private final String[] signature;

    public InvokeOperation(final ObjectName objectName, final String operationName,
                           final Object[] params, final String[] signature) {
        this.objectName = objectName;
        this.operationName = operationName;
        this.params = params;
        this.signature = signature;
    }

    @Override
    public Object operate(@Nonnull final MBeanServerConnection connection) throws QueryException, ConnectionException {
        try {
            return connection.invoke(objectName, operationName, params, signature);
        } catch (InstanceNotFoundException | MBeanException | ReflectionException e) {
            throw new QueryException("could not invoke operation", e);
        } catch (IOException e) {
            throw new ConnectionException("could not invoke operation", e);
        }
    }
}
