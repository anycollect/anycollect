package io.github.anycollect.readers.jmx.application;

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

    public Credentials(@Nonnull final String username, @Nonnull final String password) {
        Objects.requireNonNull(username, "username must not be null");
        Objects.requireNonNull(password, "password must not be null");
        this.username = username;
        this.password = password;
    }
}
