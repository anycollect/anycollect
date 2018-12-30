package io.github.anycollect.readers.jmx.monitoring;

import io.github.anycollect.metric.MetricId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.function.DoubleSupplier;

import static io.github.anycollect.readers.jmx.monitoring.MonitoringConstants.DOMAIN;

public final class JmxMetricRegistry implements MetricRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(JmxMetricRegistry.class);
    private final MBeanServer server;

    public JmxMetricRegistry() {
        this(ManagementFactory.getPlatformMBeanServer());
    }

    public JmxMetricRegistry(@Nonnull final MBeanServer server) {
        this.server = server;
    }

    @Override
    public void register(@Nonnull final MetricId id, @Nonnull final DoubleSupplier value) {
        ObjectName objectName = JmxUtils.convert(DOMAIN, id);
        MetricValue mBean = new MetricValue(value);
        try {
            server.registerMBean(mBean, objectName);
        } catch (InstanceAlreadyExistsException e) {
            LOG.debug("mbean {} ({}) has been already registered", objectName, id, e);
        } catch (MBeanRegistrationException e) {
            LOG.debug("mbean {} ({}) cannot be registered", objectName, id, e);
        } catch (NotCompliantMBeanException e) {
            LOG.debug("mbean {} ({}) is not compliant for server", objectName, id, e);
        }
    }
}
