package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.readers.jmx.config.JavaAppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class JmxConnectionFactoryImpl implements JmxConnectionFactory {
    private static final Logger LOG = LoggerFactory.getLogger(JmxConnectionFactoryImpl.class);
    private final JmxConnectorFactory connectorFactory;
    private final JavaAppConfig config;
    private final JMXServiceURL serviceURL;

    public JmxConnectionFactoryImpl(@Nonnull final JavaAppConfig config) throws MalformedURLException {
        this(JmxConnectorFactory.DEFAULT, config);
    }

    JmxConnectionFactoryImpl(@Nonnull final JmxConnectorFactory connectorFactory, @Nonnull final JavaAppConfig config)
            throws MalformedURLException {
        this.config = config;
        this.serviceURL = new JMXServiceURL(config.getUrl());
        this.connectorFactory = connectorFactory;
    }

    @Nonnull
    @Override
    public JmxConnection createJmxConnection() throws ConnectionException {
        Map<String, Object> environment = new HashMap<>();
        // TODO socket factory, timeouts, ssl, subscribe to notifications?
        config.getCredentials().ifPresent(creds ->
                environment.put(JMXConnector.CREDENTIALS, new String[]{creds.getUsername(), creds.getPassword()
                }));
        try {
            JMXConnector connector = connectorFactory.connect(serviceURL, environment);
            MBeanServerConnection connection = connector.getMBeanServerConnection();
            return new JmxConnection(connector, connection);
        } catch (IOException e) {
            LOG.debug("could not connect to {}, url: {}", config.getInstanceId(), serviceURL, e);
            throw new ConnectionException("could not connect to " + config.getInstanceId()
                    + " url: " + serviceURL, e);
        }
    }
}
