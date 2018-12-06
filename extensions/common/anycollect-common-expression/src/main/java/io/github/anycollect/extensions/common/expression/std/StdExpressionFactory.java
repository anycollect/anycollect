package io.github.anycollect.extensions.common.expression.std;

import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.common.expression.Expression;
import io.github.anycollect.extensions.common.expression.ExpressionFactory;
import io.github.anycollect.extensions.common.expression.filters.Filter;
import io.github.anycollect.extensions.common.expression.parser.ParseException;

import java.util.List;

@Extension(name = "StdExpressions", point = ExpressionFactory.class)
public final class StdExpressionFactory implements ExpressionFactory {
    private final List<Filter> filters;

    @ExtCreator
    public StdExpressionFactory(@ExtDependency(qualifier = "filters") final List<Filter> filters) {
        this.filters = filters;
    }

    @Override
    public Expression create(final String expressionString) throws ParseException {
        return new StdExpression(expressionString, filters);
    }
}
