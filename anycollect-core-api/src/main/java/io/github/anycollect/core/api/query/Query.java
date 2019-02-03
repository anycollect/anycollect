package io.github.anycollect.core.api.query;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Optional;

public interface Query {
    @Nonnull
    String getGroup();

    @Nonnull
    String getLabel();

    @Nonnull
    Optional<Duration> getInterval();

    @Nonnull
    String describe();
}
