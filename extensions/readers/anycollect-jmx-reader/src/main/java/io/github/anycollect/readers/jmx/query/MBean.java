package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.core.api.measurable.Measurable;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ObjectName;
import java.util.HashMap;
import java.util.Map;

public class MBean implements Measurable {
    private final Map<String, String> properties;
    private final Map<String, Object> attributes;
    private final Tags targetTags;

    public MBean(final ObjectName objectName, final AttributeList attributes, final Tags targetTags) {
        this.properties = new HashMap<>(objectName.getKeyPropertyList());
        this.targetTags = targetTags;
        this.attributes = new HashMap<>();
        for (Attribute attribute : attributes.asList()) {
            this.attributes.put(attribute.getName(), attribute.getValue());
        }
    }

    @Override
    @Nullable
    public String getTag(@Nonnull final String path) {
        return properties.get(path);
    }

    @Override
    @Nullable
    public Object getValue(@Nonnull final String path) {
        return traverse(path);
    }

    @Override
    @Nullable
    public String getUnit(@Nonnull final String path) {
        return (String) traverse(path);
    }

    @Nonnull
    @Override
    public Tags getTags() {
        return targetTags;
    }

    private Object traverse(final String path) {
        // TODO nested
        return attributes.get(path);
    }
}
