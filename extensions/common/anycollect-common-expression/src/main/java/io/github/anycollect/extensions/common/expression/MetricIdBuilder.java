package io.github.anycollect.extensions.common.expression;

import io.github.anycollect.metric.MetricId;

public interface MetricIdBuilder {
    MetricId create(Args context) throws EvaluationException;
}
