package io.github.anycollect.core.impl.writers.slf4j;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.core.api.Writer;
import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.core.impl.serializers.AnyCollectSerializer;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.annotations.InstanceId;
import io.github.anycollect.metric.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Extension(name = Slf4jWriter.NAME, point = Writer.class)
public class Slf4jWriter implements Writer {
    public static final String NAME = "Slf4jWriter";
    private static final Logger LOG = LoggerFactory.getLogger(Slf4jWriter.class);
    @Nonnull
    private final Serializer serializer;
    private final String id;

    @ExtCreator
    public Slf4jWriter(@ExtDependency(qualifier = "serializer", optional = true) @Nullable final Serializer serializer,
                       @InstanceId @Nonnull final String id) {
        if (serializer != null) {
            this.serializer = serializer;
        } else {
            this.serializer = new AnyCollectSerializer();
        }
        this.id = id;
    }

    @Override
    public void write(@Nonnull final List<? extends Metric> metrics) {
        for (Metric metric : metrics) {
            try {
                LOG.info("{}", serializer.serialize(metric));
            } catch (SerialisationException e) {
                LOG.debug("could not serialize metric {}", metric);
            }
        }
    }

    @Override
    public String getId() {
        return id;
    }
}
