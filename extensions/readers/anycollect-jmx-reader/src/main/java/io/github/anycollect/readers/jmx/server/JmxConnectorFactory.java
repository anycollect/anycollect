package io.github.anycollect.readers.jmx.server;

import javax.annotation.Nonnull;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Map;

public interface JmxConnectorFactory {
    JmxConnectorFactory DEFAULT = JMXConnectorFactory::connect;

    JMXConnector connect(@Nonnull JMXServiceURL url, @Nonnull Map<String, Object> environment)
            throws IOException;
}
