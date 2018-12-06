package io.github.anycollect.extensions.common.expression.ast;

import io.github.anycollect.extensions.common.expression.EvaluationException;
import io.github.anycollect.extensions.common.expression.ast.visitor.ExpressionNodeVisitor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString(doNotUseGetters = true)
public final class ComplexStringExpressionNode implements ValueExpressionNode {
    private final List<ValueExpressionNode> parts;


    public ComplexStringExpressionNode() {
        this.parts = new ArrayList<>();
    }

    public void add(final ValueExpressionNode part) {
        this.parts.add(part);
    }

    @Override
    public String getValue() throws EvaluationException {
        StringBuilder accumulator = new StringBuilder();
        for (ValueExpressionNode part : parts) {
            accumulator.append(part.getValue());
        }
        return accumulator.toString();
    }

    @Override
    public boolean isResolved() {
        return parts.stream().allMatch(ValueExpressionNode::isResolved);
    }

    @Override
    public void accept(final ExpressionNodeVisitor visitor) {
        visitor.visit(this);
        for (ValueExpressionNode part : parts) {
            part.accept(visitor);
        }
    }
}
