package io.github.anycollect.core.impl.router;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.internal.Clock;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public class RouterConfig {
    private final int poolSize;
    private final Clock clock;

    @JsonCreator
    public RouterConfig(@JsonProperty("poolSize") final int poolSize,
                        @JacksonInject @Nonnull final Clock clock) {
        this.poolSize = poolSize;
        this.clock = clock;
    }
}
