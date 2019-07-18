package io.github.anycollect.test;

import io.github.anycollect.assertj.MetricAssert;
import io.github.anycollect.core.api.Writer;
import io.github.anycollect.metric.Tags;

public interface Interceptor extends Writer {
    default void start() {

    }

    default void stop() {

    }

    MetricAssert intercepted(String key);

    MetricAssert intercepted(String key, Tags tags);

    MetricAssert intercepted(String key, Tags tags, Tags meta);
}
