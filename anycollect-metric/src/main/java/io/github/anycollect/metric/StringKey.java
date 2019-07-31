package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;

@EqualsAndHashCode(of = "normalized")
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
        format.reset();
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

    @Override
    public int length() {
        return normalized.length();
    }

    @Override
    public char charAt(final int index) {
        return normalized.charAt(index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return normalized.subSequence(start, end);
    }

    @Nonnull
    @Override
    public String toString() {
        return normalized;
    }
}
