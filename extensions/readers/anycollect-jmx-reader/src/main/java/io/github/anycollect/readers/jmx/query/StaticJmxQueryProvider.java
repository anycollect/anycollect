package io.github.anycollect.readers.jmx.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Extension(name = StaticJmxQueryProvider.NAME, point = JmxQueryProvider.class)
public class StaticJmxQueryProvider implements JmxQueryProvider {
    public static final String NAME = "StaticJmxQueryProvider";
    private final Set<JmxQuery> queries;

    @ExtCreator
    public StaticJmxQueryProvider(@ExtConfig @Nonnull final JmxQueries config) {
        this.queries = new HashSet<>(config.queries);
    }

    @Nonnull
    @Override
    public Set<JmxQuery> provide() {
        return Collections.unmodifiableSet(queries);
    }

    public static class JmxQueries {
        private final List<JmxQuery> queries;

        @JsonCreator
        public JmxQueries(@JsonProperty("queries") @Nonnull final List<JmxQuery> queries) {
            this.queries = queries;
        }
    }
}
