package io.github.anycollect.extensions.common.expression.std;

import io.github.anycollect.extensions.common.expression.Args;
import io.github.anycollect.extensions.common.expression.EvaluationException;
import io.github.anycollect.extensions.common.expression.Expression;
import io.github.anycollect.extensions.common.expression.ast.visitor.SetVariables;
import io.github.anycollect.extensions.common.expression.filters.Filter;
import io.github.anycollect.extensions.common.expression.filters.FilterAdapter;
import io.github.anycollect.extensions.common.expression.parser.*;
import io.github.anycollect.extensions.common.expression.ast.ValueExpressionNode;
import io.github.anycollect.extensions.common.expression.ast.visitor.ExpressionNodeVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.stream.Collectors.joining;

public final class StdExpression implements Expression {
    private final ValueExpressionNode root;

    public StdExpression(final String expressionString, final List<Filter> filters) throws ParseException {
        HashMap<String, FilterStrategy> strategies = new HashMap<>();
        List<String> aliases = new ArrayList<>();
        for (Filter filter : filters) {
            FilterAdapter strategy = new FilterAdapter(filter);
            strategies.put(filter.getExpression(), strategy);
            aliases.add(filter.getExpression());
            for (String alias : filter.getAliases()) {
                strategies.put(alias, strategy);
                aliases.add(alias);
            }
        }
        Tokenizer tokenizer = Tokenizer.builder()
                .add(TokenType.FILTER, aliases.stream().collect(joining("|")))
                .add(TokenType.CONSTANT, "\"[a-zA-Z\\.\\-\\+_\\\\]+\"|[a-zA-Z\\.\\-\\+_\\\\]+|true|false|[0-9]+")
                .add(TokenType.DOUBLE_QUOTES, "\"")
                .add(TokenType.OPEN_BRACKET, "\\(")
                .add(TokenType.CLOSE_BRACKET, "\\)")
                .add(TokenType.PIPE, "\\|")
                .add(TokenType.VARIABLE, "\\$\\{[a-zA-Z0-9\\.]+}")
                .add(TokenType.COLON, ",")
                .build();

        Parser parser = new Parser(tokenizer, strategies);
        root = parser.parse(expressionString);
    }

    @Override
    public String process(final Args args) throws EvaluationException {
        root.accept(new SetVariables(args));
        String value = root.getValue();
        root.accept(ExpressionNodeVisitor.reset());
        return value;
    }
}
