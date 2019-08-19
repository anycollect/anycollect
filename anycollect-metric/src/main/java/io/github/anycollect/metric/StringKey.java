package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import java.util.Objects;

final class StringKey implements Key {
    private final String normalized;

    StringKey(final String normalized) {
        this.normalized = normalized;
    }

    StringKey(final String... normalized) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (final String word : normalized) {
            if (!first) {
                builder.append(".");
            }
            builder.append(word);
            first = false;
        }
        this.normalized = builder.toString();
    }

    StringKey(final Key prefix, final Key suffix) {
        this.normalized = prefix.normalize() + "/" + suffix.normalize();
    }

    @Override
    public String normalize() {
        return normalized;
    }

    @Override
    public void print(@Nonnull final CaseFormat format, @Nonnull final StringBuilder output) {
        format.startDomain(output);
        format.startWord(output);
        for (int i = 0; i < normalized.length(); i++) {
            if (normalized.charAt(i) == '/') {
                format.finishWord(output);
                format.finishDomain(output);
                format.startDomain(output);
                format.startWord(output);
            } else if (normalized.charAt(i) == '.') {
                format.finishWord(output);
                format.startWord(output);
            } else {
                format.print(normalized.charAt(i), output);
            }
        }
        format.finishWord(output);
        format.finishDomain(output);
    }

    @Nonnull
    @Override
    public String toString() {
        return normalized;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Key)) {
            return false;
        }
        final Key that = (Key) o;
        return Objects.equals(normalized, that.normalize());
    }

    @Override
    public int hashCode() {
        return Objects.hash(normalized);
    }
}
