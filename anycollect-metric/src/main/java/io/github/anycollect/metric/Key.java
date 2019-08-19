package io.github.anycollect.metric;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * Represents a hierarchical key that is used as time series key or tag key.
 * <p>
 * The slash character ("/") is used to separate domains (namespaces)
 * E.g. jvm/gc/pause/duration
 * The dot character (".") is used to separate words in domain (namespace)
 * E.g. jvm/gc/concurrent.phase/duration
 * Word should match regexp [a-z]*
 * <p>
 * Key is intended to be simple converted to different case formats (camel case, snake case, etc)
 * and storage schemas (hierarchical / multidimensional)
 */
@Immutable
@API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
public interface Key {
    static Key empty() {
        return new StringKey("");
    }

    static Key of(@Nonnull String normalized) {
        Objects.requireNonNull(normalized, "key must not be null");
        return new StringKey(normalized);
    }

    @Nonnull
    default Key withPrefix(@Nonnull Key prefix) {
        return join(prefix, this);
    }

    @Nonnull
    default Key withPrefix(@Nonnull String word) {
        return join(new StringKey(word), this);
    }

    @Nonnull
    default Key withPrefix(@Nonnull String... phrase) {
        if (phrase.length == 0) {
            return this;
        }
        return join(new StringKey(phrase), this);
    }

    @Nonnull
    default Key withSuffix(@Nonnull Key suffix) {
        return join(this, suffix);
    }

    @Nonnull
    default Key withSuffix(@Nonnull String word) {
        return join(this, new StringKey(word));
    }

    @Nonnull
    default Key withSuffix(@Nonnull String... phrase) {
        return join(this, new StringKey(phrase));
    }

    static Key join(@Nonnull Key prefix, @Nonnull Key suffix) {
        if (prefix.isEmpty()) {
            if (suffix.isEmpty()) {
                return empty();
            } else {
                return suffix;
            }
        } else {
            if (suffix.isEmpty()) {
                return prefix;
            } else {
                return new JoinKey(prefix, suffix);
            }
        }
    }

    String normalize();

    default boolean isEmpty() {
        return normalize().isEmpty();
    }

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
        public final void startDomain(@Nonnull final StringBuilder output) {
            if (!firstDomain) {
                separateDomains(output);
            }
        }

        @Override
        public final void startWord(@Nonnull final StringBuilder output) {
            if (!firstWordInDomain) {
                separateWordsInDomain(output);
            }
            firstCharInWord = true;
        }

        @Override
        public final void print(@Nonnull final CharSequence sequence,
                                final int start, final int end,
                                @Nonnull final StringBuilder output) {
            for (int i = start; i < end; i++) {
                print(sequence.charAt(i), output);
            }
        }

        @Override
        public final void print(final char elem, @Nonnull final StringBuilder output) {
            print(elem, firstDomain, firstWordInDomain, firstCharInWord, output);
            firstCharInWord = false;
        }


        @Override
        public final void finishWord(@Nonnull final StringBuilder output) {
            if (firstWordInDomain) {
                firstWordInDomain = false;
            }
        }

        @Override
        public final void finishDomain(@Nonnull final StringBuilder output) {
            if (firstDomain) {
                firstDomain = false;
            }
            firstWordInDomain = true;
        }

        @Override
        public final void reset() {
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
        protected void print(final char elem,
                             final boolean firstDomain,
                             final boolean firstWordInDomain,
                             final boolean firstCharInWord,
                             @Nonnull final StringBuilder output) {
            output.append(elem);
        }
    }
}
