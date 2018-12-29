package io.github.anycollect.readers.jmx.application;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.Objects;

@ToString
@EqualsAndHashCode
public final class ConcurrencyLevel {
    @Nonnull
    private final String name;
    private final int maxNumberOfThreads;

    public ConcurrencyLevel(@Nonnull final String name, final int maxNumberOfThreads) {
        Objects.requireNonNull(name, "name of concurrency level must not be null");
        if (maxNumberOfThreads < 1) {
            throw new IllegalArgumentException("maximal number of threads must be at least one");
        }
        this.name = name;
        this.maxNumberOfThreads = maxNumberOfThreads;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public int getMaxNumberOfThreads() {
        return maxNumberOfThreads;
    }
}
