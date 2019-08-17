package io.github.anycollect.meter.api;

import io.github.anycollect.metric.Key;
import io.github.anycollect.metric.Tags;
import org.junit.jupiter.api.Test;

import java.util.function.ToLongFunction;

import static org.mockito.Mockito.*;

class FunctionCounterTest {
    @Test
    void registerFunctionCounter() {
        Object obj = new Object();
        ToLongFunction<Object> value = o -> 1;
        MeterRegistry registry = mock(MeterRegistry.class);
        FunctionCounter.make("test", obj, value)
                .unit("tests")
                .register(registry);
        verify(registry, times(1)).counter(
                new ImmutableMeterId(Key.of("test"), "tests", Tags.empty(), Tags.empty()),
                obj,
                value
        );
    }
}