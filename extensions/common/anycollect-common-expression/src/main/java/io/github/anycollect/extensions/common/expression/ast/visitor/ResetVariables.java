package io.github.anycollect.extensions.common.expression.ast.visitor;

import io.github.anycollect.extensions.common.expression.ast.VariableExpressionNode;

public final class ResetVariables implements ExpressionNodeVisitor {
    public static final ResetVariables INSTANCE = new ResetVariables();

    private ResetVariables() {
    }

    @Override
    public void visit(final VariableExpressionNode variable) {
        variable.reset();
    }
}
