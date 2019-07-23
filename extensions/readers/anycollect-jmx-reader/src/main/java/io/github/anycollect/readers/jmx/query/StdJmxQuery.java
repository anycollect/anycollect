package io.github.anycollect.readers.jmx.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.metric.prepared.PreparedMetric;
import io.github.anycollect.metric.prepared.PreparedMetricBuilder;
import io.github.anycollect.readers.jmx.query.operations.QueryAttributes;
import io.github.anycollect.readers.jmx.query.operations.QueryObjectNames;
import io.github.anycollect.readers.jmx.query.operations.QueryOperation;
import io.github.anycollect.readers.jmx.server.JavaApp;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.management.Attribute;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import java.util.*;

@Getter
@EqualsAndHashCode(callSuper = true)
public final class StdJmxQuery extends JmxQuery {
    private static final Logger LOG = LoggerFactory.getLogger(StdJmxQuery.class);
    private final Clock clock;
    private final String key;
    @Nonnull
    private final String unit;
    private final List<String> tagKeys;
    private final ObjectName objectPattern;
    @Nonnull
    private final Restriction restriction;
    private final String[] attributeNames;
    private final List<MeasurementPath> paths;

    @JsonCreator
    public StdJmxQuery(@JsonProperty(value = "key", required = true) @Nonnull final String key,
                       @JsonProperty("tags") @Nullable final Tags tags,
                       @JsonProperty("meta") @Nullable final Tags meta,
                       @JsonProperty("unit") @Nullable final String unit,
                       @JsonProperty("tagKeys") @Nullable final List<String> tagKeys,
                       @JsonProperty(value = "mbean", required = true) @Nonnull final String objectPattern,
                       @JsonProperty("whitelist") @Nullable final Whitelist whitelist,
                       @JsonProperty("measurements") @Nonnull final List<MeasurementPath> paths) {
        super(key, tags != null ? tags : Tags.empty(), meta != null ? meta : Tags.empty());
        this.clock = Clock.getDefault();
        this.key = key;
        this.unit = unit != null ? unit : "";
        this.tagKeys = tagKeys != null ? tagKeys : Collections.emptyList();
        List<String> activeAttributes = new ArrayList<>();
        for (MeasurementPath measurement : paths) {
            activeAttributes.add(measurement.getAttribute());
        }
        try {
            this.objectPattern = new ObjectName(objectPattern);
        } catch (MalformedObjectNameException e) {
            throw new ConfigurationException("object name " + objectPattern + " is malformed", e);
        }
        if (whitelist != null) {
            this.restriction = whitelist;
        } else {
            this.restriction = Restriction.all();
        }
        this.attributeNames = activeAttributes.toArray(new String[0]);
        this.paths = paths;
    }

    @Nonnull
    @Override
    public Job bind(@Nonnull final JavaApp app) {
        return new JobImpl(app);
    }

    private final class JobImpl implements Job {
        private final JavaApp app;
        private final QueryOperation<Set<ObjectName>> queryNames;
        private final Map<ObjectName, PreparedMetric> cache;

        JobImpl(final JavaApp app) {
            this.app = app;
            this.queryNames = new QueryObjectNames(objectPattern);
            this.cache = new HashMap<>();
        }

        @Override
        public List<Metric> execute() throws QueryException, ConnectionException {
            Set<ObjectName> objectNames = app.operate(queryNames);
            if (objectNames.isEmpty()) {
                LOG.warn("No mbeans matched to {}", objectPattern);
            }
            List<Metric> metrics = new ArrayList<>();
            for (ObjectName objectName : objectNames) {
                if (restriction.allows(objectName)) {
                    List<Attribute> attributes = app.operate(new QueryAttributes(objectName, attributeNames));
                    if (attributes.size() != attributeNames.length) {
                        List<String> missingAttributes = Arrays.asList(attributeNames);
                        for (final Attribute attribute : attributes) {
                            missingAttributes.remove(attribute.getName());
                        }
                        LOG.warn("Missing some attributes in {}, did not retrieve: {}. This mbean will be skipped",
                                objectName,
                                missingAttributes);
                        continue;
                    }
                    long timestamp = clock.wallTime();
                    PreparedMetric metric = cache.computeIfAbsent(objectName, this::prepare);
                    if (metric == null) {
                        continue;
                    }
                    double[] values = new double[attributes.size()];
                    for (int i = 0; i < attributes.size(); ++i) {
                        Object value = attributes.get(i).getValue();
                        List<String> valuePath = paths.get(i).getValuePath();
                        for (int part = 1; part < valuePath.size(); part++) {
                            String node = valuePath.get(part);
                            if (value instanceof CompositeData) {
                                ((CompositeData) value).get(node);
                            } else if (value instanceof TabularData) {
                                TabularData tabularValue = (TabularData) value;
                                @SuppressWarnings("unchecked")
                                Collection<CompositeData> tableData = (Collection<CompositeData>) tabularValue.values();
                                for (CompositeData compositeData : tableData) {
                                    if (compositeData.get("key").equals(node)) {
                                        value = compositeData.get("value");
                                        break;
                                    }
                                }
                            }
                        }
                        if (value instanceof Number) {
                            values[i] = ((Number) value).doubleValue();
                        } else if (value instanceof Boolean) {
                            values[i] = ((boolean) value) ? 1 : 0;
                        }
                    }
                    metrics.add(metric.compile(timestamp, values));
                }
            }
            return metrics;
        }

        private PreparedMetric prepare(final ObjectName objectName) {
            PreparedMetricBuilder builder = Metric.prepare()
                    .key(key)
                    .concatTags(app.getTags())
                    .concatTags(getTags())
                    .concatMeta(app.getMeta())
                    .concatMeta(getMeta());
            for (String tagKey : tagKeys) {
                String tagValue = objectName.getKeyProperty(tagKey);
                if (tagValue == null) {
                    LOG.warn("Could not create metric from {}, property {} is missing.",
                            objectName, tagKey);
                    return null;
                }
                builder.tag(tagKey, tagValue);
            }
            for (MeasurementPath path : paths) {
                builder.measurement(path.getStat(), path.getType(), unit);
            }
            return builder.build();
        }
    }
}
