package io.github.anycollect.readers.jmx.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode
public final class Credentials {
    private final String username;
    private final String password;

    @JsonCreator
    public Credentials(@JsonProperty("username") @Nonnull final String username,
                       @JsonProperty("password") @Nonnull final String password) {
        Objects.requireNonNull(username, "username must not be null");
        Objects.requireNonNull(password, "password must not be null");
        this.username = username;
        this.password = password;
    }
}
