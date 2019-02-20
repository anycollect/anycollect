package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.core.api.query.QueryProvider;
import io.github.anycollect.extensions.annotations.ExtPoint;

@ExtPoint
public interface JmxQueryProvider extends QueryProvider<JmxQuery> {
}
