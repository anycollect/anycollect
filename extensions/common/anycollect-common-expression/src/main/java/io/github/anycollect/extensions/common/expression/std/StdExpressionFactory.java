package io.github.anycollect.extensions.common.expression.std;

import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.common.expression.ExpressionFactory;
import io.github.anycollect.extensions.common.expression.ParseException;
import io.github.anycollect.extensions.common.expression.filters.Filter;
import io.github.anycollect.extensions.common.expression.filters.FilterAdapter;
import io.github.anycollect.extensions.common.expression.parser.FilterStrategy;
import io.github.anycollect.extensions.common.expression.parser.Parser;
import io.github.anycollect.extensions.common.expression.parser.TokenType;
import io.github.anycollect.extensions.common.expression.parser.Tokenizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Extension(name = "StdExpressions", contracts = ExpressionFactory.class)
public final class StdExpressionFactory implements ExpressionFactory {
    private final Parser parser;

    @ExtCreator
    public StdExpressionFactory(@ExtDependency(qualifier = "filters") final List<Filter> filters) {
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
        Tokenizer.Builder builder = Tokenizer.builder();
        if (!filters.isEmpty()) {
            builder.add(TokenType.FILTER, String.join("|", aliases));
        }
        Tokenizer tokenizer = builder
                .add(TokenType.CONSTANT, "\"[/a-zA-Z\\.\\-\\+_\\\\]+\"|[/a-zA-Z\\.\\-\\+_\\\\]+|true|false|[0-9]+")
                .add(TokenType.DOUBLE_QUOTES, "\"")
                .add(TokenType.OPEN_BRACKET, "\\(")
                .add(TokenType.CLOSE_BRACKET, "\\)")
                .add(TokenType.PIPE, "\\|")
                .add(TokenType.VARIABLE, "\\$\\{[a-zA-Z0-9\\._]+}")
                .add(TokenType.COLON, ",")
                .build();

        this.parser = new Parser(tokenizer, strategies);
    }

    public StdExpressionFactory() {
        this(Collections.emptyList());
    }

    @Override
    public StdExpression create(final String expressionString) throws ParseException {
        return new StdExpression(parser.parse(expressionString));
    }
}
