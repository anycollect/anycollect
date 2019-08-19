package io.github.anycollect.core.impl.serializers.graphite;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Extension(name = GraphiteSerializer.NAME, contracts = Serializer.class)
public final class GraphiteSerializer implements Serializer {
    public static final String NAME = "GraphiteSerializer";
    private final GraphiteSerializerConfig config;
    private final Key.CaseFormat keyFormat;
    private final Key.CaseFormat tagFormat;
    private final StringBuilder builder = new StringBuilder(1024);
    private final CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
    private final Map<String, Key> escapedWords = new HashMap<>();

    @ExtCreator
    public GraphiteSerializer(@ExtConfig(optional = true) @Nullable final GraphiteSerializerConfig optConfig) {
        this.config = optConfig != null ? optConfig : GraphiteSerializerConfig.DEFAULT;
        this.keyFormat = new GraphiteKeyCaseFormat();
        this.tagFormat = new GraphiteTagCaseFormat();
    }

    @Override
    public CoderResult serialize(@Nonnull final Sample sample, @Nonnull final ByteBuffer buffer)
            throws SerialisationException {
        builder.setLength(0);
        doSerialize(sample);
        return encoder.encode(CharBuffer.wrap(builder), buffer, true);
    }

    private void doSerialize(@Nonnull final Sample sample) {
        if (Double.isNaN(sample.getValue())) {
            return;
        }
        Tags tags = config.tags().concat(sample.getTags());
        Key key = sample.getKey();
        builder.setLength(0);
        Key prefix = config.prefix();
        for (final Key asPrefix : config.tagsAsPrefix()) {
            if (tags.hasTagKey(asPrefix)) {
                prefix = prefix.withSuffix(asPrefix);
                String rawValue = tags.getTagValue(asPrefix);
                prefix = prefix.withSuffix(escapedWords.computeIfAbsent(rawValue, GraphiteSerializer::escapeWord));
            }
        }
        key = key.withPrefix(prefix)
                .withSuffix(escapedWords.computeIfAbsent(sample.getUnit(), GraphiteSerializer::escapeWord))
                .withSuffix(escapedWords.computeIfAbsent(sample.getStat().getTagValue(), GraphiteSerializer::escapeWord));
        keyFormat.reset();
        key.print(keyFormat, builder);
        if (!tags.isEmpty()) {
            if (!config.tagSupport()) {
                for (Tag tag : tags) {
                    if (config.tagsAsPrefix().contains(tag.getKey())) {
                        continue;
                    }
                    builder.append(".");
                    tagFormat.reset();
                    tag.getKey().print(tagFormat, builder);
                    builder.append(".");
                    normalize(tag.getValue(), builder);
                }
            } else {
                for (Tag tag : tags) {
                    if (config.tagsAsPrefix().contains(tag.getKey())) {
                        continue;
                    }
                    builder.append(";");
                    tagFormat.reset();
                    tag.getKey().print(tagFormat, builder);
                    builder.append("=");
                    normalize(tag.getValue(), builder);
                }
            }
        }
        builder.append(" ");
        builder.append(sample.getValue());
        builder.append(" ");
        long timestamp = TimeUnit.MILLISECONDS.toSeconds(sample.getTimestamp());
        builder.append(timestamp);
        builder.append("\n");
    }

    private static Key escapeWord(final String raw) {
        StringBuilder tmp = new StringBuilder();
        normalize(raw, tmp);
        return Key.of(tmp.toString());
    }

    private static void normalize(final String source, final StringBuilder builder) {
        boolean upper = false;
        for (int i = 0; i < source.length(); ++i) {
            char ch = source.charAt(i);
            if (ch == '.') {
                upper = true;
                continue;
            }
            if (ch == ' ') {
                builder.append('_');
            } else {
                if (upper) {
                    builder.append(Character.toUpperCase(ch));
                    upper = false;
                } else {
                    builder.append(ch);
                }
            }
        }
    }

    private static class GraphiteKeyCaseFormat extends Key.StatefulCaseFormat {
        @Override
        protected void separateDomains(@Nonnull final StringBuilder output) {
            output.append('.');
        }

        @Override
        protected void separateWordsInDomain(@Nonnull final StringBuilder output) {
        }

        @Override
        protected void print(final char elem,
                             final boolean firstDomain,
                             final boolean firstWordInDomain,
                             final boolean firstCharInWord,
                             @Nonnull final StringBuilder output) {
            if (!firstWordInDomain && firstCharInWord) {
                output.append(Character.toUpperCase(elem));
            } else {
                output.append(elem);
            }
        }
    }

    private static class GraphiteTagCaseFormat extends Key.StatefulCaseFormat {
        @Override
        protected void separateDomains(@Nonnull final StringBuilder output) {
        }

        @Override
        protected void separateWordsInDomain(@Nonnull final StringBuilder output) {
        }

        @Override
        protected void print(final char elem,
                             final boolean firstDomain,
                             final boolean firstWordInDomain,
                             final boolean firstCharInWord,
                             @Nonnull final StringBuilder output) {
            if ((!firstWordInDomain || !firstDomain) && firstCharInWord) {
                output.append(Character.toUpperCase(elem));
            } else {
                output.append(elem);
            }
        }
    }
}
