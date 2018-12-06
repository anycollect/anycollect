package io.github.anycollect.extensions.common.expression.ast;

import io.github.anycollect.extensions.common.expression.ast.visitor.ExpressionNodeVisitor;

public interface ExpressionNode {
    void accept(ExpressionNodeVisitor visitor);
}
