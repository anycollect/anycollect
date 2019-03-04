package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import io.github.anycollect.metric.*;

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
        serializers.addSerializer(PointId.class, new MetricIdSerializer());
        serializers.addSerializer(Point.class, new MetricSerializer());
        serializers.addSerializer(Stat.class, new StatSerializer());
        serializers.addSerializer(Type.class, new TypeSerializer());
        context.addSerializers(serializers);

        SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(Tags.class, new TagsDeserializer());
        deserializers.addDeserializer(PointId.class, new MetricIdDeserializer());
        deserializers.addDeserializer(Point.class, new MetricDeserializer());
        deserializers.addDeserializer(Stat.class, new StatDeserializer());
        deserializers.addDeserializer(Type.class, new TypeDeserializer());
        context.addDeserializers(deserializers);
    }
}
