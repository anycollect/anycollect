package io.github.anycollect.readers.jmx.monitoring;

import io.github.anycollect.metric.MetricId;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Tag;
import io.github.anycollect.metric.Type;

import javax.annotation.Nonnull;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Hashtable;
import java.util.Map;

public final class JmxUtils {
    private JmxUtils() {
        throw new IllegalStateException();
    }

    public static MetricId convert(@Nonnull final ObjectName objectName) {
        Map<String, String> keys = objectName.getKeyPropertyList();
        MetricId.Builder builder = MetricId.builder();
        if (keys.containsKey(MetricId.METRIC_KEY_TAG)) {
            builder.key(keys.get(MetricId.METRIC_KEY_TAG));
        }
        if (keys.containsKey(MetricId.METRIC_TYPE_TAG)) {
            builder.type(Type.parse(keys.get(MetricId.METRIC_TYPE_TAG)));
        }
        if (keys.containsKey(MetricId.STAT_TAG)) {
            builder.stat(Stat.parse(keys.get(MetricId.STAT_TAG)));
        }
        if (keys.containsKey(MetricId.UNIT_TAG)) {
            builder.unit(keys.get(MetricId.UNIT_TAG));
        }
        for (Map.Entry<String, String> entry : keys.entrySet()) {
            String tagKey = entry.getKey();
            String tagValue = entry.getValue();
            if (!MetricId.isSpecialTag(tagKey)) {
                builder.tag(tagKey, tagValue);
            }
        }
        return builder.build();
    }

    public static ObjectName convert(final String domain, final MetricId id) {
        Hashtable<String, String> tags = new Hashtable<>();
        for (Tag tag : id.getTags()) {
            tags.put(tag.getKey(), tag.getValue());
        }
        try {
            return new ObjectName(domain, tags);
        } catch (MalformedObjectNameException e) {
            throw new IllegalArgumentException("domain or id are wrong", e);
        }
    }
}
