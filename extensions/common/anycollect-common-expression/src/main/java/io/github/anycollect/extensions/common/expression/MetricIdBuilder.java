package io.github.anycollect.extensions.common.expression;

import io.github.anycollect.metric.PointId;

public interface MetricIdBuilder {
    PointId create(Args context) throws EvaluationException;
}
