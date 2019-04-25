package io.github.anycollect.core.impl.pull.availability;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.concurrent.Immutable;

@Getter
@ToString
@Immutable
@EqualsAndHashCode
public final class Check {
    private final Health health;
    private final long timestamp;

    public static Check passed(final long timestamp) {
        return new Check(Health.PASSED, timestamp);
    }

    public static Check failed(final long timestamp) {
        return new Check(Health.FAILED, timestamp);
    }

    public static Check unknown(final long timestamp) {
        return new Check(Health.UNKNOWN, timestamp);
    }

    private Check(final Health health, final long timestamp) {
        this.health = health;
        this.timestamp = timestamp;
    }
}
