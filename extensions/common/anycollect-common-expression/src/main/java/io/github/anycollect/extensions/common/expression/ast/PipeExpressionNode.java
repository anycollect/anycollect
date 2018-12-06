package io.github.anycollect.extensions.common.expression.ast;

import io.github.anycollect.extensions.common.expression.EvaluationException;
import io.github.anycollect.extensions.common.expression.ast.visitor.ExpressionNodeVisitor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public final class PipeExpressionNode implements ValueExpressionNode {
    private final ValueExpressionNode base;
    private final List<FilterExpressionNode> filters = new ArrayList<>();

    public PipeExpressionNode(final ValueExpressionNode base) {
        this.base = base;
    }

    public void add(final FilterExpressionNode node) {
        this.filters.add(node);
    }

    @Override
    public void accept(final ExpressionNodeVisitor visitor) {
        visitor.visit(this);
        base.accept(visitor);
        for (FilterExpressionNode filter : filters) {
            filter.accept(visitor);
        }
    }

    @Override
    public String getValue() throws EvaluationException {
        String value = base.getValue();
        for (FilterExpressionNode filter : filters) {
            value = filter.filter(value);
        }
        return value;
    }

    @Override
    public boolean isResolved() {
        return base.isResolved();
    }
}
