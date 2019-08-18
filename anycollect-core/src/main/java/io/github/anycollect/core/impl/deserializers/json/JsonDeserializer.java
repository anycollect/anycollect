package io.github.anycollect.core.impl.deserializers.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.anycollect.core.api.Deserializer;
import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.jackson.AnyCollectModule;
import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;
import java.io.IOException;

@Extension(name = JsonDeserializer.NAME, contracts = Deserializer.class)
public final class JsonDeserializer implements Deserializer {
    public static final String NAME = "JsonDeserializer";
    private final ObjectMapper objectMapper;

    @ExtCreator
    public JsonDeserializer() {
        this.objectMapper = new ObjectMapper(new JsonFactory());
        objectMapper.registerModule(new AnyCollectModule());
    }

    @Nonnull
    @Override
    public Sample deserialize(@Nonnull final String string) throws SerialisationException {
        try {
            return objectMapper.readValue(string, Sample.class);
        } catch (IOException e) {
            throw new SerialisationException("could not deserializer metric", e);
        }
    }
}
