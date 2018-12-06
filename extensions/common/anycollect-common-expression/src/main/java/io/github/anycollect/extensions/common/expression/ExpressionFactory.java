package io.github.anycollect.extensions.common.expression;

import io.github.anycollect.extensions.annotations.ExtPoint;
import io.github.anycollect.extensions.common.expression.parser.ParseException;

@ExtPoint
public interface ExpressionFactory {
    Expression create(String expressionString) throws ParseException;
}
