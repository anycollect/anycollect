package io.github.anycollect.core.api.query;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Optional;

public abstract class AbstractQuery implements Query {
    private final String group;
    private final String label;
    private final Duration duration;

    public AbstractQuery(@Nonnull final String group, @Nonnull final String label, @Nullable final Duration duration) {
        this.group = group;
        this.label = label;
        this.duration = duration;
    }

    @Nonnull
    @Override
    public final String getGroup() {
        return group;
    }

    @Nonnull
    @Override
    public final String getLabel() {
        return label;
    }

    @Nonnull
    @Override
    public final Optional<Duration> getInterval() {
        return Optional.ofNullable(duration);
    }

    @Nonnull
    @Override
    public final String describe() {
        return group + ":" + label;
    }
}
