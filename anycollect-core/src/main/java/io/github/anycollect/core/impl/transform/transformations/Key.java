package io.github.anycollect.core.impl.transform.transformations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public final class Key {
    private final String source;
    private final String target;

    @JsonCreator
    public Key(@JsonProperty("source") @Nonnull final String source,
               @JsonProperty("target") @Nonnull final String target) {
        this.source = source;
        this.target = target;
    }
}
