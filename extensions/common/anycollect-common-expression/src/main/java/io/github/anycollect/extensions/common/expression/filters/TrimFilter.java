package io.github.anycollect.extensions.common.expression.filters;

import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.common.expression.ArgValidationException;

import java.util.Collections;
import java.util.List;

import static io.github.anycollect.extensions.common.expression.filters.TrimFilter.NAME;

@Extension(name = NAME, point = Filter.class)
public final class TrimFilter extends BaseFilter {
    public static final String NAME = "TrimFilter";

    @ExtCreator
    public TrimFilter(@ExtConfig(key = "aliases") final List<String> aliases) {
        super("trim", aliases);
    }

    public TrimFilter() {
        super("trim", Collections.emptyList());
    }

    @Override
    public String filter(final String source, final List<String> args) throws ArgValidationException {
        if (!args.isEmpty()) {
            throw new ArgValidationException(NAME + " filter requires no arguments, given: " + args);
        }
        return source.trim();
    }
}
