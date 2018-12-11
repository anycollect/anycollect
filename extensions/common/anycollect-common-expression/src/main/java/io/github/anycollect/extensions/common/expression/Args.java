package io.github.anycollect.extensions.common.expression;

import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@EqualsAndHashCode
public final class Args {
    private final Map<String, String> args;

    public static Builder builder() {
        return new Builder();
    }

    private Args(final Builder builder) {
        this.args = new HashMap<>(builder.args);
    }

    public boolean contains(final String key) {
        Objects.requireNonNull(key, "key must not be null");
        return args.containsKey(key);
    }

    public String get(final String key) {
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
            return new Args(this);
        }
    }
}
