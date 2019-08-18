package io.github.anycollect.extensions.common.expression.filters;

import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Extension(name = JoinFilter.NAME, contracts = Filter.class)
public final class JoinFilter extends BaseFilter {
    public static final String NAME = "JoinFilter";

    @ExtCreator
    public JoinFilter(@ExtConfig(key = "aliases") final List<String> aliases) {
        super("join", aliases);
    }

    public JoinFilter() {
        super("join", Collections.emptyList());
    }

    @Override
    public String filter(final String source, final List<String> args) {
        return source + args.stream().collect(joining());
    }
}
