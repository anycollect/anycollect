package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.common.Plugin;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

public interface QueryMatcherResolver<T extends Target<Q>, Q extends Query> extends Plugin, Lifecycle {
    QueryMatcher<T, Q> current();
}
