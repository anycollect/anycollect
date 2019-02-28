package io.github.anycollect.metric.noop;

import io.github.anycollect.metric.FunctionCounter;
import io.github.anycollect.metric.MeterId;

import javax.annotation.Nonnull;

public class NoopFunctionCounter extends NoopAbstractMeter implements FunctionCounter {
    public NoopFunctionCounter(@Nonnull final MeterId id) {
        super(id);
    }
}
