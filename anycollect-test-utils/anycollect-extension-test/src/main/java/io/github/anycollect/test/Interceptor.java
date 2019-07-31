package io.github.anycollect.test;

import io.github.anycollect.assertj.SampleAssert;
import io.github.anycollect.core.api.Writer;
import io.github.anycollect.metric.Tags;

public interface Interceptor extends Writer {
    default void start() {

    }

    default void stop() {

    }

    SampleAssert intercepted(String key);

    SampleAssert intercepted(String key, Tags tags);

    SampleAssert intercepted(String key, Tags tags, Tags meta);
}
