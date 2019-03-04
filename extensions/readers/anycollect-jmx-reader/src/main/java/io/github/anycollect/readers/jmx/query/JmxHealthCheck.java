package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.metric.*;

import javax.annotation.Nonnull;
import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JmxHealthCheck extends JmxQuery {
    public JmxHealthCheck() {
        super("jmx.healthCheck");
    }

    @Nonnull
    @Override
    public List<Metric> executeOn(@Nonnull final MBeanServerConnection connection,
                                  @Nonnull final Tags targetTags) throws ConnectionException {
        try {
            connection.getDefaultDomain();
        } catch (IOException e) {
            throw new ConnectionException("health check is failed", e);
        }
        return Collections.emptyList();
    }
}
