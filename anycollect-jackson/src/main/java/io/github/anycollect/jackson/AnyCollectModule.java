package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.*;

@Extension(
        name = AnyCollectModule.NAME,
        contracts = Module.class,
        autoload = @Extension.AutoLoad(instanceName = AnyCollectModule.INSTANCE_NAME)
)
public final class AnyCollectModule extends Module {
    public static final String NAME = "AnyCollectJacksonModule";
    public static final String INSTANCE_NAME = "anycollectJacksonModule";

    @Override
    public String getModuleName() {
        return "AnyCollectModule";
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(final SetupContext context) {
        SimpleSerializers serializers = new SimpleSerializers();
        serializers.addSerializer(Tags.class, new TagsSerializer());
        serializers.addSerializer(Key.class, new KeySerializer());
        serializers.addSerializer(Sample.class, new SampleSerializer());
        serializers.addSerializer(Stat.class, new StatSerializer());
        context.addSerializers(serializers);

        SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(Tags.class, new TagsDeserializer());
        deserializers.addDeserializer(Key.class, new KeyDeserializer());
        deserializers.addDeserializer(Sample.class, new SampleDeserializer());
        deserializers.addDeserializer(Stat.class, new StatDeserializer());
        context.addDeserializers(deserializers);
    }
}
