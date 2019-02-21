package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;
import java.util.*;

public final class ImmutableState<T extends Target<Q>, Q extends Query> implements State<T, Q> {
    private final Map<T, Set<PeriodicQuery<Q>>> state;

    public static <T extends Target<Q>, Q extends Query> ImmutableState.Builder<T, Q> builder() {
        return new Builder<>();
    }

    private ImmutableState(final Builder<T, Q> builder) {
        // TODO fix mutability, make deep copy
        this.state = builder.state;
    }

    @Override
    public Set<T> getTargets() {
        return Collections.unmodifiableSet(state.keySet());
    }

    @Override
    public Set<PeriodicQuery<Q>> getQueries(@Nonnull final T target) {
        return Collections.unmodifiableSet(state.getOrDefault(target, Collections.emptySet()));
    }

    public static final class Builder<T extends Target<Q>, Q extends Query> {
        private final Map<T, Set<PeriodicQuery<Q>>> state = new HashMap<>();

        public Builder<T, Q> put(@Nonnull final T target, @Nonnull final Q query, @Nonnull final int period) {
            Set<PeriodicQuery<Q>> queries = state.computeIfAbsent(target, t -> new HashSet<>());
            queries.add(new ImmutablePeriodicQuery<>(query, period));
            return this;
        }

        public Builder<T, Q> put(@Nonnull final T target) {
            state.putIfAbsent(target, new HashSet<>());
            return this;
        }

        public ImmutableState<T, Q> build() {
            return new ImmutableState<>(this);
        }
    }
}
