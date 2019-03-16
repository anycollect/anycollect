package io.github.anycollect.readers.jmx.query;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.measurable.FamilyConfig;
import io.github.anycollect.core.api.measurable.Measurer;
import io.github.anycollect.core.api.measurable.Measurers;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.jmx.server.JavaApp;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.management.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
public final class StdJmxQuery extends JmxQuery {
    private static final Logger LOG = LoggerFactory.getLogger(StdJmxQuery.class);
    private final Clock clock;
    private final ObjectName objectPattern;
    private final Restriction restriction;
    private final List<Measurer<MBean>> measurers;
    private final String[] attributes;

    // TODO is it ok to instantiate such classes in configuration phase?
    @JsonCreator
    public StdJmxQuery(@JsonProperty(value = "queryId", required = true) @Nonnull final String queryId,
                       @JsonProperty("tags") @Nullable final Tags tags,
                       @JsonProperty("meta") @Nullable final Tags meta,
                       @JsonProperty("mbean") @Nonnull final String objectPattern,
                       @JsonProperty("restriction") @Nullable final Restriction restriction,
                       @JsonProperty("families") @Nonnull final List<FamilyConfig> families,
                       @JacksonInject @Nonnull final Measurers types,
                       @JacksonInject @Nonnull final Clock clock) {
        super(queryId, tags != null ? tags : Tags.empty(), meta != null ? meta : Tags.empty());
        this.clock = clock;
        measurers = new ArrayList<>();
        Set<String> activeAttributes = new HashSet<>();
        for (FamilyConfig family : families) {
            if (!types.hasDefinition(family.getMetricFamilyName())) {
                LOG.warn("could not find definition for {}, query {} will not be executed",
                        family.getMetricFamilyName(),
                        queryId);
            } else {
                Measurer<MBean> measurer = types.make(family);
                measurers.add(measurer);
                activeAttributes.addAll(measurer.getPaths());
            }
        }
        try {
            this.objectPattern = new ObjectName(objectPattern);
        } catch (MalformedObjectNameException e) {
            // TODO replace to configuration exception
            throw new RuntimeException(e);
        }
        if (restriction != null) {
            this.restriction = restriction;
        } else {
            this.restriction = Restriction.all();
        }
        this.attributes = activeAttributes.toArray(new String[0]);
    }

    @Nonnull
    @Override
    public List<Metric> executeOn(@Nonnull final MBeanServerConnection connection,
                                  @Nonnull final JavaApp app)
            throws QueryException, ConnectionException {
        Set<ObjectName> objectNames = queryNames(connection, objectPattern);
        List<Metric> metricFamilies = new ArrayList<>();
        for (ObjectName objectName : objectNames) {
            if (restriction.allows(objectName)) {
                AttributeList attributeList;
                try {
                    attributeList = connection.getAttributes(objectName, this.attributes);
                } catch (InstanceNotFoundException | ReflectionException | IOException e) {
                    throw new ConnectionException("could not get attributes", e);
                }
                long timestamp = clock.wallTime();
                // TODO maybe cache, do benchmark?
                MBean mbean = new MBean(objectName, attributeList,
                        Tags.concat(app.getTags(), getTags()),
                        Tags.concat(app.getMeta(), getMeta()));
                for (Measurer<MBean> measurer : measurers) {
                    metricFamilies.add(measurer.measure(mbean, timestamp));
                }
            }
        }
        return metricFamilies;
    }
}
