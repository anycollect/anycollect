package io.github.anycollect.extensions.common.expression.parser;

import io.github.anycollect.extensions.common.expression.EvaluationException;

import java.util.List;

public interface FilterStrategy {
    String filter(String source, List<String> args) throws EvaluationException;
}
