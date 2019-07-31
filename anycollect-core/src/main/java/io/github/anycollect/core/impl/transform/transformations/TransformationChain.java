package io.github.anycollect.core.impl.transform.transformations;

import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;
import java.util.List;

public final class TransformationChain implements Transformation {
    private final List<Transformation> transformations;

    public TransformationChain(@Nonnull final List<Transformation> transformations) {
        this.transformations = transformations;
    }

    @Nonnull
    @Override
    public Sample transform(@Nonnull final Sample source) {
        Sample target = source;
        for (Transformation transformation : transformations) {
            target = transformation.transform(target);
        }
        return target;
    }
}
