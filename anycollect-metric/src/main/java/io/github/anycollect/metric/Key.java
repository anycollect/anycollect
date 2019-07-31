package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import java.util.Objects;

public interface Key extends CharSequence {
    static Key of(@Nonnull String normalized) {
        Objects.requireNonNull(normalized, "key must not be null");
        return new StringKey(normalized);
    }

    @Nonnull
    default Key withPrefix(@Nonnull Key prefix) {
        return new StringKey(prefix, this);
    }

    @Nonnull
    default Key withPrefix(@Nonnull String word) {
        if (word.isEmpty()) {
            return this;
        }
        return withPrefix(new StringKey(word));
    }

    @Nonnull
    default Key withPrefix(@Nonnull String... phrase) {
        if (phrase.length == 0) {
            return this;
        }
        return withPrefix(new StringKey(phrase));
    }

    @Nonnull
    default Key withSuffix(@Nonnull String word) {
        return new StringKey(this, new StringKey(word));
    }

    @Nonnull
    default Key withSuffix(@Nonnull String... phrase) {
        return new StringKey(this, new StringKey(phrase));
    }

    default boolean contentEquals(@Nonnull final CharSequence that) {
        if (this == that) {
            return true;
        }
        if (hashCode() == that.hashCode()) {
            if (this.equals(that)) {
                return true;
            }
        }
        if (this.length() != that.length()) {
            return false;
        }
        int length = this.length();
        for (int i = 0; i < length; i++) {
            if (this.charAt(i) != that.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    String normalize();

    void print(@Nonnull CaseFormat format, @Nonnull StringBuilder output);

    default String toString(@Nonnull CaseFormat format) {
        return toString(this, format);
    }

    static String toString(@Nonnull Key key, @Nonnull CaseFormat format) {
        StringBuilder stringBuilder = new StringBuilder();
        key.print(format, stringBuilder);
        return stringBuilder.toString();
    }

    interface CaseFormat {
        void startDomain(@Nonnull StringBuilder output);

        void startWord(@Nonnull StringBuilder output);

        default void print(@Nonnull CharSequence sequence, @Nonnull StringBuilder output) {
            print(sequence, 0, sequence.length(), output);
        }

        void print(@Nonnull CharSequence sequence, int start, int end, @Nonnull StringBuilder output);

        void print(char elem, @Nonnull StringBuilder output);

        void finishWord(@Nonnull StringBuilder output);

        void finishDomain(@Nonnull StringBuilder output);

        void reset();
    }

    abstract class StatefulCaseFormat implements CaseFormat {
        private boolean firstDomain = true;
        private boolean firstWordInDomain = true;
        private boolean firstCharInWord = true;

        @Override
        public void startDomain(@Nonnull final StringBuilder output) {
            if (!firstDomain) {
                separateDomains(output);
            }
        }

        @Override
        public void startWord(@Nonnull final StringBuilder output) {
            if (!firstWordInDomain) {
                separateWordsInDomain(output);
            }
            firstCharInWord = true;
        }

        @Override
        public void print(@Nonnull final CharSequence sequence,
                          final int start, final int end,
                          @Nonnull final StringBuilder output) {
            for (int i = start; i < end; i++) {
                print(sequence.charAt(i), firstDomain, firstWordInDomain, firstCharInWord, output);
            }
        }

        @Override
        public void print(final char elem, @Nonnull final StringBuilder output) {
            print(elem, firstDomain, firstWordInDomain, firstCharInWord, output);
            firstCharInWord = false;
        }


        @Override
        public void finishWord(@Nonnull final StringBuilder output) {
            if (firstWordInDomain) {
                firstWordInDomain = false;
            }
        }

        @Override
        public void finishDomain(@Nonnull final StringBuilder output) {
            if (firstDomain) {
                firstDomain = false;
            }
            firstWordInDomain = true;
        }

        @Override
        public void reset() {
            firstDomain = true;
            firstWordInDomain = true;
            firstCharInWord = true;
        }

        protected void separateDomains(@Nonnull final StringBuilder output) {
        }

        protected void separateWordsInDomain(@Nonnull final StringBuilder output) {
        }

        protected void print(final char elem,
                             final boolean firstDomain,
                             final boolean firstWordInDomain,
                             final boolean firstCharInWord,
                             @Nonnull final StringBuilder output) {
            output.append(elem);
        }
    }

    final class StdCaseFormat extends StatefulCaseFormat {
        @Override
        protected void separateDomains(@Nonnull final StringBuilder output) {
            output.append('/');
        }

        @Override
        protected void separateWordsInDomain(@Nonnull final StringBuilder output) {
            output.append('.');
        }

        @Override
        protected void print(final char elem, final boolean firstDomain, final boolean firstWordInDomain, final boolean firstCharInWord, @Nonnull final StringBuilder output) {
            output.append(elem);
        }
    }
}
