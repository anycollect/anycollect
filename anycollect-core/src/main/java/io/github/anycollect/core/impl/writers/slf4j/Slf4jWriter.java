package io.github.anycollect.core.impl.writers.slf4j;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.core.api.Writer;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.MetricFamily;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Extension(name = Slf4jWriter.NAME, point = Writer.class)
public class Slf4jWriter implements Writer {
    public static final String NAME = "Slf4jWriter";
    private static final Logger LOG = LoggerFactory.getLogger(Slf4jWriter.class);
    private final Serializer serializer;

    @ExtCreator
    public Slf4jWriter(@ExtDependency(qualifier = "format", optional = true) @Nullable final Serializer serializer) {
        if (serializer != null) {
            this.serializer = serializer;
        } else {
            this.serializer = new Serializer() {
                @Nonnull
                @Override
                public String serialize(@Nonnull final MetricFamily family) {
                    return family.toString();
                }
            };
        }
    }

    @Override
    public void write(@Nonnull final List<MetricFamily> families) {
        for (MetricFamily family : families) {
            LOG.info("{}", serializer.serialize(family));
        }
    }
}