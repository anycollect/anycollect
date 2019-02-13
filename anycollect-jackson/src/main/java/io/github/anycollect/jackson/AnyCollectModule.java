package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.MetricId;
import io.github.anycollect.metric.Tags;

public final class AnyCollectModule extends Module {
    @Override
    public String getModuleName() {
        return "anycollect";
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(final SetupContext context) {
        SimpleSerializers serializers = new SimpleSerializers();
        serializers.addSerializer(Tags.class, new TagsSerializer());
        serializers.addSerializer(MetricId.class, new MetricIdSerializer());
        serializers.addSerializer(Metric.class, new MetricSerializer());
        context.addSerializers(serializers);

        SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(Tags.class, new TagsDeserializer());
        deserializers.addDeserializer(MetricId.class, new MetricIdDeserializer());
        deserializers.addDeserializer(Metric.class, new MetricDeserializer());
        context.addDeserializers(deserializers);
    }
}
