package io.github.anycollect.metric;

import org.junit.jupiter.api.Test;

import java.util.function.ToDoubleFunction;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class FunctionCounterTest {
    @Test
    void registerFunctionCounter() {
        Object obj = new Object();
        ToDoubleFunction<Object> value = o -> 1.0;
        MeterRegistry registry = mock(MeterRegistry.class);
        FunctionCounter.make("test", obj, value)
                .unit("tests")
                .register(registry);
        verify(registry, times(1)).counter(
                MeterId.key("test").unit("tests").build(),
                obj,
                value
        );
    }
}