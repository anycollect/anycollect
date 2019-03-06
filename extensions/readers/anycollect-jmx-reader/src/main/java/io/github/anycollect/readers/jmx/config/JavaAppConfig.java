package io.github.anycollect.readers.jmx.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.Optional;

@ToString
@EqualsAndHashCode
public final class JavaAppConfig {
    private static final String URL_FRONT = "service:jmx:rmi:///jndi/rmi://";
    private static final String URL_BACK = "/jmxrmi";
    @Getter
    private final String instanceId;
    @Getter
    private final String url;
    private final String host;
    private final int port;
    private final Credentials credentials;

    public JavaAppConfig(@Nullable final String instanceId,
                         @Nullable final String url,
                         @Nullable final Credentials credentials) {
        this(instanceId, url, null, null, credentials);
    }

    @JsonCreator
    public JavaAppConfig(@JsonProperty("id") @Nullable final String instanceId,
                         @JsonProperty("url") @Nullable final String url,
                         @JsonProperty("host") @Nullable final String host,
                         @JsonProperty("port") @Nullable final Integer port,
                         @JsonProperty("credentials") @Nullable final Credentials credentials) {
        this.instanceId = instanceId;
        this.credentials = credentials;
        this.host = host;
        this.port = port != null ? port : -1;
        this.url = url != null ? url : URL_FRONT + this.host + ":" + this.port + URL_BACK;
    }

    public Optional<Credentials> getCredentials() {
        return Optional.ofNullable(credentials);
    }
}
