package io.github.anycollect.extensions.common.expression.ast;

import io.github.anycollect.extensions.common.expression.EvaluationException;
import io.github.anycollect.extensions.common.expression.ast.visitor.ExpressionNodeVisitor;
import lombok.ToString;

@ToString(doNotUseGetters = true)
public final class ArgumentExpressionNode implements ValueExpressionNode {
    private final ValueExpressionNode value;

    public ArgumentExpressionNode(final ValueExpressionNode value) {
        this.value = value;
    }

    @Override
    public void accept(final ExpressionNodeVisitor visitor) {
        visitor.visit(this);
        value.accept(visitor);
    }

    @Override
    public String getValue() throws EvaluationException {
        return value.getValue();
    }

    @Override
    public boolean isResolved() {
        return value.isResolved();
    }
}
