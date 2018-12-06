package io.github.anycollect.extensions.common.expression.ast;

import io.github.anycollect.extensions.common.expression.EvaluationException;
import io.github.anycollect.extensions.common.expression.ast.visitor.ExpressionNodeVisitor;
import io.github.anycollect.extensions.common.expression.parser.FilterStrategy;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@ToString
public final class FilterExpressionNode implements ExpressionNode {
    private final String name;
    private final ArgumentsExpressionNode arguments;
    private final FilterStrategy strategy;

    public FilterExpressionNode(final String name, final FilterStrategy strategy) {
        this.name = name;
        this.strategy = strategy;
        this.arguments = null;
    }

    public FilterExpressionNode(final String name,
                                final ArgumentsExpressionNode arguments,
                                final FilterStrategy strategy) {
        this.strategy = strategy;
        Objects.requireNonNull(arguments, "arguments must not be null");
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public void accept(final ExpressionNodeVisitor visitor) {
        visitor.visit(this);
        if (arguments != null) {
            arguments.accept(visitor);
        }
    }

    public String getName() {
        return name;
    }

    public String filter(final String source) throws EvaluationException {
        if (arguments == null) {
            return strategy.filter(source, Collections.emptyList());
        }
        List<String> resolvedArguments = new ArrayList<>();
        for (ArgumentExpressionNode argument : arguments) {
            resolvedArguments.add(argument.getValue());
        }
        return strategy.filter(source, resolvedArguments);
    }
}
