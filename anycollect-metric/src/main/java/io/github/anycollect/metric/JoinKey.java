package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class JoinKey implements Key {
    private final Key prefix;
    private final Key suffix;

    public JoinKey(final Key prefix, final Key suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public String normalize() {
        return prefix.normalize() + "/" + suffix.normalize();
    }

    @Override
    public void print(@Nonnull final CaseFormat format, @Nonnull final StringBuilder output) {
        prefix.print(format, output);
        suffix.print(format, output);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof JoinKey) {
            final JoinKey joinKey = (JoinKey) o;
            return Objects.equals(prefix, joinKey.prefix)
                    && Objects.equals(suffix, joinKey.suffix);
        } else if (o instanceof Key) {
            Key that = (Key) o;
            return Objects.equals(this.normalize(), that.normalize());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, suffix);
    }
}
