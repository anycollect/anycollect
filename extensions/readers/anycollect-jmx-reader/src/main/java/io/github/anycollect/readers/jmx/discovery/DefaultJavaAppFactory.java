package io.github.anycollect.readers.jmx.discovery;

import io.github.anycollect.core.api.target.TargetCreationException;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.readers.jmx.config.JavaAppConfig;
import io.github.anycollect.readers.jmx.server.JavaApp;
import io.github.anycollect.readers.jmx.server.JmxConnectionFactory;
import io.github.anycollect.readers.jmx.server.JmxConnectionFactoryImpl;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPoolFactory;
import io.github.anycollect.readers.jmx.server.pool.impl.CommonsJmxConnectionPoolFactory;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;

public class DefaultJavaAppFactory implements JavaAppFactory {
    private final JmxConnectionPoolFactory factory;
    private final MeterRegistry registry;

    public DefaultJavaAppFactory(@Nonnull final MeterRegistry registry) {
        factory = new CommonsJmxConnectionPoolFactory();
        this.registry = registry;
    }

    @Nonnull
    @Override
    public JavaApp create(@Nonnull final JavaAppConfig definition) throws TargetCreationException {
        try {
            JmxConnectionFactory connectionFactory = new JmxConnectionFactoryImpl(definition);
            return JavaApp.create(
                    definition.getInstanceId(),
                    factory.create(connectionFactory),
                    registry);
        } catch (MalformedURLException e) {
            throw new TargetCreationException("could not create java target", e);
        }
    }
}