package io.github.anycollect.extensions.common.expression.filters;


import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.common.expression.ArgValidationException;

import java.util.Collections;
import java.util.List;

@Extension(name = MatchReplaceFilter.NAME, point = Filter.class)
public final class MatchReplaceFilter extends BaseFilter {
    public static final String NAME = "MatchReplaceFilter";

    @ExtCreator
    public MatchReplaceFilter(@ExtConfig(key = "aliases") final List<String> aliases) {
        super("replace", aliases);
    }

    public MatchReplaceFilter() {
        super("replace", Collections.emptyList());
    }

    @Override
    public String filter(final String source, final List<String> args) throws ArgValidationException {
        if (args.size() != 2) {
            throw new ArgValidationException(NAME + " filter requires two arguments, given: " + args);
        }
        String regex = args.get(0);
        String replacement = args.get(1);
        return source.replaceAll(regex, replacement);
    }
}
