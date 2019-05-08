package io.github.anycollect.extensions.common.expression.std;

import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.common.expression.Expression;
import io.github.anycollect.extensions.common.expression.ExpressionFactory;
import io.github.anycollect.extensions.common.expression.filters.Filter;
import io.github.anycollect.extensions.common.expression.ParseException;

import java.util.Collections;
import java.util.List;

@Extension(name = "StdExpressions", point = ExpressionFactory.class)
public final class StdExpressionFactory implements ExpressionFactory {
    private final List<Filter> filters;

    @ExtCreator
    public StdExpressionFactory(@ExtDependency(qualifier = "filters") final List<Filter> filters) {
        this.filters = filters;
    }

    public StdExpressionFactory() {
        this.filters = Collections.emptyList();
    }

    @Override
    public Expression create(final String expressionString) throws ParseException {
        return new StdExpression(expressionString, filters);
    }
}
