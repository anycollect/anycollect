package io.github.anycollect.extensions.common.expression.ast;

import io.github.anycollect.extensions.common.expression.EvaluationException;

public interface ValueExpressionNode extends ExpressionNode {
    String getValue() throws EvaluationException;

    boolean isResolved();
}
