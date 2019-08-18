package io.github.anycollect.core.impl.writers.slf4j;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.core.api.Writer;
import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.core.impl.serializers.AnyCollectSerializer;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.annotations.InstanceId;
import io.github.anycollect.metric.Sample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Extension(name = Slf4jWriter.NAME, contracts = Writer.class)
public class Slf4jWriter implements Writer, Lifecycle {
    public static final String NAME = "Slf4jWriter";
    private static final Logger LOG = LoggerFactory.getLogger(Slf4jWriter.class);
    private static final Logger WRITER = LoggerFactory.getLogger("Slf4j");
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
    public void write(@Nonnull final List<? extends Sample> metrics) {
        for (Sample sample : metrics) {
            try {
                MDC.put("slf4j.writer.instance.id", id);
                WRITER.info("{}", serializer.serialize(sample));
            } catch (SerialisationException e) {
                LOG.debug("could not serialize metric {}", sample);
            } finally {
                MDC.clear();
            }
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void init() {
        LOG.info("{} has been successfully initialised", NAME);
    }

    @Override
    public void destroy() {
        LOG.info("{}({}) has been successfully destroyed", id, NAME);
    }
}
