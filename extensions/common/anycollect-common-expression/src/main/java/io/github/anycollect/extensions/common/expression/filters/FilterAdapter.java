package io.github.anycollect.extensions.common.expression.filters;

import io.github.anycollect.extensions.common.expression.EvaluationException;
import io.github.anycollect.extensions.common.expression.parser.FilterStrategy;

import java.util.List;

public final class FilterAdapter implements FilterStrategy {
    private final Filter filter;

    public FilterAdapter(final Filter filter) {
        this.filter = filter;
    }

    @Override
    public String filter(final String source, final List<String> args) throws EvaluationException {
        return filter.filter(source, args);
    }
}
