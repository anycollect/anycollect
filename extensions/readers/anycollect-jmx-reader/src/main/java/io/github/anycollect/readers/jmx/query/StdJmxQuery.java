package io.github.anycollect.readers.jmx.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.job.TaggingJob;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.*;
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
    private final Key key;
    private final List<String> tagKeys;
    private final ObjectName objectPattern;
    @Nonnull
    private final Restriction restriction;
    private final String[] attributeNames;
    private final List<MeasurementPath> paths;
    private final MetricFactory factory;

    @JsonCreator
    public StdJmxQuery(@JsonProperty(value = "key", required = true) @Nonnull final Key key,
                       @JsonProperty("tags") @Nullable final Tags tags,
                       @JsonProperty("meta") @Nullable final Tags meta,
                       @JsonProperty("tagKeys") @Nullable final List<String> tagKeys,
                       @JsonProperty(value = "mbean", required = true) @Nonnull final String objectPattern,
                       @JsonProperty("whitelist") @Nullable final Whitelist whitelist,
                       @JsonProperty("measurements") @Nonnull final List<MeasurementPath> paths) {
        this(key, tags, meta, tagKeys, objectPattern, whitelist, paths, null);
    }

    public StdJmxQuery(@Nonnull final Key key,
                       @Nullable final Tags tags,
                       @Nullable final Tags meta,
                       @Nullable final List<String> tagKeys,
                       @Nonnull final String objectPattern,
                       @Nullable final Whitelist whitelist,
                       @Nonnull final List<MeasurementPath> paths,
                       @Nullable final MetricFactory factory) {
        super(key.toString(), tags != null ? tags : Tags.empty(), meta != null ? meta : Tags.empty());
        this.clock = Clock.getDefault();
        this.key = key;
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
        this.factory = factory != null ? factory : new StdMetricIdFactory();
    }

    @Nonnull
    @Override
    public Job bind(@Nonnull final JavaApp app) {
        return new TaggingJob(app, this, new JobImpl(app));
    }

    private final class JobImpl implements Job {
        private final JavaApp app;
        private final QueryOperation<Set<ObjectName>> queryNames;
        private final Map<ObjectName, List<Metric>> cache;

        JobImpl(final JavaApp app) {
            this.app = app;
            this.queryNames = new QueryObjectNames(objectPattern);
            this.cache = new HashMap<>();
        }

        @Override
        public List<Sample> execute() throws QueryException, ConnectionException {
            Set<ObjectName> objectNames = app.operate(queryNames);
            if (objectNames.isEmpty()) {
                LOG.warn("No mbeans matched to {}", objectPattern);
            }
            List<Sample> samples = new ArrayList<>();
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
                    List<Metric> ids = cache.computeIfAbsent(objectName, this::prepare);
                    if (ids == null) {
                        continue;
                    }
                    for (int i = 0; i < attributes.size(); ++i) {
                        Metric id = ids.get(i);
                        if (id == null) {
                            continue;
                        }
                        Object value = attributes.get(i).getValue();
                        List<String> valuePath = paths.get(i).getValuePath();
                        for (int part = 1; part < valuePath.size(); part++) {
                            String node = valuePath.get(part);
                            if (value instanceof CompositeData) {
                                value = ((CompositeData) value).get(node);
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
                        if (value instanceof Long) {
                            samples.add(id.sample((long) value, timestamp));
                        } else if (value instanceof Number) {
                            samples.add(id.sample(((Number) value).doubleValue(), timestamp));
                        } else if (value instanceof Boolean) {
                            samples.add(id.sample(((boolean) value) ? 1 : 0, timestamp));
                        }
                    }
                }
            }
            return samples;
        }

        private List<Metric> prepare(@Nonnull final ObjectName objectName) {
            List<Metric> ids = new ArrayList<>();
            for (final MeasurementPath attribute : paths) {
                ids.add(factory.create(app, objectName, attribute));
            }
            return ids;
        }
    }

    public interface MetricFactory {
        @Nullable
        Metric create(@Nonnull Target target, @Nonnull ObjectName objectName, @Nonnull MeasurementPath attribute);
    }

    private class StdMetricIdFactory implements MetricFactory {
        @Override
        @Nullable
        public Metric create(@Nonnull final Target target, @Nonnull final ObjectName objectName, @Nonnull final MeasurementPath attribute) {
            ImmutableTags.Builder tags = Tags.builder();
            for (String tagKey : tagKeys) {
                String tagValue = objectName.getKeyProperty(tagKey);
                if (tagValue == null) {
                    LOG.warn("Could not create metric from {}, property {} is missing.",
                            objectName, tagKey);
                    return null;
                }
                tags.tag(tagKey, tagValue);
            }
            Metric.MetricBuilder builder = Metric.builder()
                    .key(key)
                    .tags(tags.build())
                    .empty();
            return builder.metric(attribute.getStat(), attribute.getType(), attribute.getUnit());
        }
    }
}
