package io.github.anycollect.extensions.common.expression.ast;

import io.github.anycollect.extensions.common.expression.EvaluationException;
import io.github.anycollect.extensions.common.expression.ast.visitor.ExpressionNodeVisitor;
import lombok.ToString;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ToString(doNotUseGetters = true)
public final class VariableExpressionNode implements ValueExpressionNode {
    private static final Pattern VAR = Pattern.compile("\\$\\{(.*)}");
    private final String name;
    private String value;

    public VariableExpressionNode(final String sequence) {
        Matcher matcher = VAR.matcher(sequence);
        if (matcher.find()) {
            name = matcher.group(1);
        } else {
            throw new IllegalArgumentException(sequence + " is not a variable expression");
        }
    }

    @Override
    public void accept(final ExpressionNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getValue() throws EvaluationException {
        if (!isResolved()) {
            throw new EvaluationException(name + "is not resolved");
        }
        return value;
    }

    @Override
    public boolean isResolved() {
        return value != null;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public void reset() {
        this.value = null;
    }

    public String getName() {
        return name;
    }

}
