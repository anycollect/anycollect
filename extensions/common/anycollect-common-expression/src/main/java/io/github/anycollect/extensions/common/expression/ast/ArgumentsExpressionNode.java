package io.github.anycollect.extensions.common.expression.ast;

import io.github.anycollect.extensions.common.expression.ast.visitor.ExpressionNodeVisitor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@ToString
public final class ArgumentsExpressionNode implements ExpressionNode, Iterable<ArgumentExpressionNode> {
    private final List<ArgumentExpressionNode> arguments;

    public ArgumentsExpressionNode(final ArgumentExpressionNode base) {
        arguments = new ArrayList<>();
        arguments.add(base);
    }

    @Override
    public void accept(final ExpressionNodeVisitor visitor) {
        visitor.visit(this);
        for (ArgumentExpressionNode argument : arguments) {
            argument.accept(visitor);
        }
    }

    public void add(final ArgumentExpressionNode argument) {
        this.arguments.add(argument);
    }

    @Override
    public Iterator<ArgumentExpressionNode> iterator() {
        return Collections.unmodifiableList(arguments).iterator();
    }
}
