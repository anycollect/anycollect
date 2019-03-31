package io.github.anycollect.core.impl.transform.transformations;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;

public final class TransformationChain implements Transformation {
    private final List<Transformation> transformations;

    public TransformationChain(@Nonnull final List<Transformation> transformations) {
        this.transformations = transformations;
    }

    @Nonnull
    @Override
    public Metric transform(@Nonnull final Metric source) {
        Metric target = source;
        for (Transformation transformation : transformations) {
            target = transformation.transform(target);
        }
        return target;
    }
}
