package io.github.anycollect.extensions.common.expression.ast.visitor;

import io.github.anycollect.extensions.common.expression.Args;
import io.github.anycollect.extensions.common.expression.ast.VariableExpressionNode;

public final class SetVariables implements ExpressionNodeVisitor {
    private final Args args;

    public SetVariables(final Args args) {
        this.args = args;
    }

    @Override
    public void visit(final VariableExpressionNode variable) {
        variable.setValue(args.get(variable.getName()));
    }
}
