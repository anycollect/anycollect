package io.github.anycollect.extensions.common.expression.ast;

import io.github.anycollect.extensions.common.expression.ast.visitor.ExpressionNodeVisitor;
import lombok.ToString;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ToString
public final class ConstantExpressionNode implements ValueExpressionNode {
    private static final Pattern CONSTANT = Pattern.compile("\"(.*)\"");
    private final String value;

    public ConstantExpressionNode(final String sequence) {
        Matcher matcher = CONSTANT.matcher(sequence);
        if (matcher.matches()) {
            value = matcher.group(1);
        } else {
            value = sequence;
        }
    }

    @Override
    public void accept(final ExpressionNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean isResolved() {
        return true;
    }
}
