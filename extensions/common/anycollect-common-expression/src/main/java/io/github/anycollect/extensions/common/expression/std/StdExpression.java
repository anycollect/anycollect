package io.github.anycollect.extensions.common.expression.std;

import io.github.anycollect.extensions.common.expression.Args;
import io.github.anycollect.extensions.common.expression.EvaluationException;
import io.github.anycollect.extensions.common.expression.Expression;
import io.github.anycollect.extensions.common.expression.ast.ValueExpressionNode;
import io.github.anycollect.extensions.common.expression.ast.visitor.ExpressionNodeVisitor;
import io.github.anycollect.extensions.common.expression.ast.visitor.SetVariables;

public final class StdExpression implements Expression {
    private final ValueExpressionNode root;

    public StdExpression(final ValueExpressionNode root) {
        this.root = root;
    }

    @Override
    public String process(final Args args) throws EvaluationException {
        root.accept(new SetVariables(args));
        String value = root.getValue();
        root.accept(ExpressionNodeVisitor.reset());
        return value;
    }
}
