package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.jmx.server.JavaApp;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class MockJavaApp extends JavaApp {
    protected MockJavaApp() {
        super("simple", Tags.empty(), Tags.empty());
    }

    public MockJavaApp(@Nonnull Tags tags) {
        super("simple", tags, Tags.empty());
    }

    @Nonnull
    @Override
    public List<Metric> execute(@Nonnull JmxQuery query) throws QueryException, ConnectionException {
        return Collections.emptyList();
    }
}
