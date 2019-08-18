package io.github.anycollect.core.impl.transform;

import io.github.anycollect.core.api.Processor;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.filter.Filter;
import io.github.anycollect.core.api.filter.FilterChain;
import io.github.anycollect.core.api.filter.FilterReply;
import io.github.anycollect.core.impl.transform.transformations.Transformation;
import io.github.anycollect.core.impl.transform.transformations.TransformationChain;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.annotations.InstanceId;
import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;
import java.util.List;

@Extension(name = Transformer.NAME, contracts = Processor.class)
public final class Transformer implements Processor {
    public static final String NAME = "Transformer";
    private final Filter filter;
    private final Transformation transformation;
    private final boolean forwardSourceMetric;
    private final String id;
    private Dispatcher dispatcher;

    @ExtCreator
    public Transformer(@ExtConfig @Nonnull final TransformerConfig config,
                       @InstanceId @Nonnull final String id) {
        this.forwardSourceMetric = config.metricSourceAction() == MetricSourceAction.KEEP;
        this.id = id;
        this.filter = new FilterChain(config.filters());
        this.transformation = new TransformationChain(config.transformations());
    }

    @Override
    public void start(@Nonnull final Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void submit(@Nonnull final List<? extends Sample> sources) {
        if (dispatcher == null) {
            return;
        }
        for (Sample source : sources) {
            if (forwardSourceMetric) {
                dispatcher.dispatch(source);
            }
            if (filter.accept(source.getMetric()) == FilterReply.ACCEPT) {
                Sample target = transformation.transform(source);
                dispatcher.dispatch(target);
            }
        }
    }

    @Override
    public String getId() {
        return id;
    }
}
