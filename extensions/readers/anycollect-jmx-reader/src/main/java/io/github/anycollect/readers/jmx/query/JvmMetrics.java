package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Extension(name = JvmMetrics.NAME, point = JmxQueryProvider.class)
public final class JvmMetrics implements JmxQueryProvider {
    public static final String NAME = "JvmMetrics";
    private final Set<JmxQuery> queries;

    @ExtCreator
    public JvmMetrics() {
        queries = new HashSet<>();
        queries.add(new JvmMemory());
        queries.add(new JvmThreads());
        queries.add(new JvmRuntime());
    }

    @Nonnull
    @Override
    public Set<JmxQuery> provide() {
        return Collections.unmodifiableSet(queries);
    }
}
