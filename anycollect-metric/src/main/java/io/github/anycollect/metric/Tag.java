package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

@Getter
@EqualsAndHashCode
public final class Tag {
    private final String key;
    private final String value;

    public static Tag of(final String key, final String value) {
        Objects.requireNonNull(key, "tag key must not be null");
        Objects.requireNonNull(value, "tag value must not be null");
        return new Tag(key, value);
    }

    private Tag(final String key, final String value) {
        this.key = key;
        this.value = value;
    }
}
