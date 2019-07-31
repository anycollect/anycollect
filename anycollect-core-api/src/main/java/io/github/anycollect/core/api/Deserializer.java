package io.github.anycollect.core.api;

import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;

public interface Deserializer {
    @Nonnull
    Sample deserialize(@Nonnull String string) throws SerialisationException;
}
