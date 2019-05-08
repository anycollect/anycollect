package io.github.anycollect.extensions.common.expression;

import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@EqualsAndHashCode
public final class MapArgs implements Args {
    private final Map<String, String> args;

    public static Builder builder() {
        return new Builder();
    }

    private MapArgs(final Builder builder) {
        this.args = new HashMap<>(builder.args);
    }

    @Override
    public boolean contains(@Nonnull final String key) {
        Objects.requireNonNull(key, "key must not be null");
        return args.containsKey(key);
    }

    @Nonnull
    @Override
    public String get(@Nonnull final String key) {
        if (!contains(key)) {
            throw new IllegalArgumentException("there is no values associated with " + key);
        }
        return args.get(key);
    }

    public static final class Builder {
        private final Map<String, String> args = new HashMap<>();

        public Builder add(final String key, final String value) {
            Objects.requireNonNull(key, "key must not be null");
            Objects.requireNonNull(value, "value must not be null");
            args.put(key, value);
            return this;
        }

        public Args build() {
            return new MapArgs(this);
        }
    }
}
