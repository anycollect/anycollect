package io.github.anycollect.readers.jmx.monitoring;

import io.github.anycollect.metric.MetricId;
import io.github.anycollect.metric.Tag;

import javax.annotation.Nonnull;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Hashtable;
import java.util.Map;

public final class JmxUtils {
    private JmxUtils() {
    }

    public static MetricId convert(@Nonnull final ObjectName objectName) {
        Map<String, String> keys = objectName.getKeyPropertyList();
        MetricId.Builder builder = MetricId.builder();
        for (Map.Entry<String, String> entry : keys.entrySet()) {
            String tagKey = entry.getKey();
            String tagValue = entry.getValue();
            builder.tag(tagKey, tagValue);
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
