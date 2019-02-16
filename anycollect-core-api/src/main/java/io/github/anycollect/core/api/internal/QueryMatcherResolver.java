package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.common.Plugin;
import io.github.anycollect.extensions.annotations.ExtPoint;

@ExtPoint
public interface QueryMatcherResolver extends Plugin, Lifecycle {
    QueryMatcher current();
}
