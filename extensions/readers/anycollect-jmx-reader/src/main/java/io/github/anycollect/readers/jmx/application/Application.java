package io.github.anycollect.readers.jmx.application;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

@ToString
@EqualsAndHashCode
public final class Application {
    @Nonnull
    private final String name;
    @Nonnull
    private final QueryMatcher queryMatcher;
    @Nullable
    private final ConcurrencyLevel concurrencyLevel;
    @Nullable
    private final Credentials credentials;
    private final boolean sslEnabled;

    public Application(@Nonnull final String name,
                       @Nonnull final QueryMatcher queryMatcher,
                       final boolean sslEnabled) {
        this(name, queryMatcher, null, null, sslEnabled);
    }

    public Application(@Nonnull final String name,
                       @Nonnull final QueryMatcher queryMatcher,
                       @Nullable final ConcurrencyLevel concurrencyLevel,
                       @Nullable final Credentials credentials,
                       final boolean sslEnabled) {
        Objects.requireNonNull(name, "name of application must not be null");
        Objects.requireNonNull(queryMatcher, "query matcher must not be null");
        this.name = name;
        this.queryMatcher = queryMatcher;
        this.concurrencyLevel = concurrencyLevel;
        this.credentials = credentials;
        this.sslEnabled = sslEnabled;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public QueryMatcher getQueryMatcher() {
        return queryMatcher;
    }

    @Nonnull
    public Optional<ConcurrencyLevel> getConcurrencyLevel() {
        return Optional.ofNullable(concurrencyLevel);
    }

    @Nonnull
    public Optional<Credentials> getCredentials() {
        return Optional.ofNullable(credentials);
    }

    public boolean isSslEnabled() {
        return sslEnabled;
    }
}
