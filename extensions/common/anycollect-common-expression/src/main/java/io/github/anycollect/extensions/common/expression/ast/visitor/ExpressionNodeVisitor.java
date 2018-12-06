package io.github.anycollect.extensions.common.expression.ast.visitor;

import io.github.anycollect.extensions.common.expression.ast.*;

public interface ExpressionNodeVisitor {
    static ExpressionNodeVisitor reset() {
        return ResetVariables.INSTANCE;
    }

    default void visit(ConstantExpressionNode constant) {
    }

    default void visit(VariableExpressionNode variable) {
    }

    default void visit(ComplexStringExpressionNode complex) {
    }

    default void visit(ArgumentExpressionNode argument) {
    }

    default void visit(ArgumentsExpressionNode arguments) {
    }

    default void visit(FilterExpressionNode filter) {
    }

    default void visit(PipeExpressionNode pipe) {
    }
}
