package io.github.anycollect.core.impl.writers.socket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public final class SocketConfig {
    private final String host;
    private final int port;
    private final Protocol protocol;

    @JsonCreator
    public SocketConfig(@JsonProperty("host") @Nonnull final String host,
                        @JsonProperty("port") final int port,
                        @JsonProperty("protocol") @Nonnull final Protocol protocol) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
    }
}
