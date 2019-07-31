package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

@Getter
@EqualsAndHashCode
public final class Tag {
    private final Key key;
    private final String value;

    public static Tag of(final String key, final String value) {
        return of(Key.of(key), value);
    }

    public static Tag of(final Key key, final String value) {
        Objects.requireNonNull(key, "tag key must not be null");
        Objects.requireNonNull(value, "tag value must not be null");
        return new Tag(key, value);
    }

    private Tag(final Key key, final String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
