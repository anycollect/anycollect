package io.github.anycollect.extensions.common.expression.filters;

import io.github.anycollect.extensions.common.expression.EvaluationException;

import java.util.List;

public interface Filter {
    String filter(String source, List<String> args) throws EvaluationException;

    String getExpression();

    List<String> getAliases();
}
