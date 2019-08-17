package io.github.anycollect.core.impl.transform.transformations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.metric.Key;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public final class KeyTransformation {
    private final Key source;
    private final Key target;

    @JsonCreator
    public KeyTransformation(@JsonProperty("source") @Nonnull final Key source,
                             @JsonProperty("target") @Nonnull final Key target) {
        this.source = source;
        this.target = target;
    }
}
