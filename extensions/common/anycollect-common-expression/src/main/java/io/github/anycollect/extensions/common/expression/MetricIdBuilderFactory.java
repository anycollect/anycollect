package io.github.anycollect.extensions.common.expression;

import io.github.anycollect.extensions.annotations.ExtPoint;

import java.util.Map;

@ExtPoint
public interface MetricIdBuilderFactory {
    MetricIdBuilder create(String key, String unit, String stat, String type,
                           Map<String, String> tagExpressions, Map<String, String> metaTagExpressions)
            throws ParseException;

}
