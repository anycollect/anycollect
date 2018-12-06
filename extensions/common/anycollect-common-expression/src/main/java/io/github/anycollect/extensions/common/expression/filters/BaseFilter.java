package io.github.anycollect.extensions.common.expression.filters;

import java.util.*;

public abstract class BaseFilter implements Filter {
    private final String expression;
    private final List<String> aliases;

    public BaseFilter(final String expression, final List<String> aliases) {
        this.expression = expression;
        this.aliases = new ArrayList<>(aliases);
    }

    @Override
    public final String getExpression() {
        return expression;
    }

    @Override
    public final List<String> getAliases() {
        return Collections.unmodifiableList(aliases);
    }
}
