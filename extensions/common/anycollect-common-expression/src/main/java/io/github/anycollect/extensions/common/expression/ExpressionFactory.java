package io.github.anycollect.extensions.common.expression;

public interface ExpressionFactory {
    Expression create(String expressionString) throws ParseException;
}
