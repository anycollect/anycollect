package io.github.anycollect.readers.jmx.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

@Getter
@ToString
@EqualsAndHashCode
public final class JavaAppConfig {
    private final String instanceId;
    private final String url;
    private final Credentials credentials;
    private final boolean ssl;

    @JsonCreator
    public JavaAppConfig(@JsonProperty("id") @Nonnull final String instanceId,
                         @JsonProperty("url") @Nonnull final String url,
                         @JsonProperty("credentials") @Nullable final Credentials credentials,
                         @JsonProperty(value = "ssl", defaultValue = "true") final boolean ssl) {
        this.instanceId = instanceId;
        this.url = url;
        this.credentials = credentials;
        this.ssl = ssl;
    }

    public Optional<Credentials> getCredentials() {
        return Optional.ofNullable(credentials);
    }
}
