package io.github.anycollect.extensions.common.expression;

public interface Expression {
    String process(Args args) throws EvaluationException;
}
