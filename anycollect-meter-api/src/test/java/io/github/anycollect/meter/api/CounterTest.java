package io.github.anycollect.meter.api;

import io.github.anycollect.metric.Key;
import io.github.anycollect.metric.Tags;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class CounterTest {
    @Test
    void registryCounter() {
        TestMeterRegistry registry = spy(TestMeterRegistry.class);
        Counter.key("my.key")
                .unit("dollars")
                .tag("key", "value")
                .meta("meta", "data")
                .register(registry);
        verify(registry, times(1)).counter(
                new ImmutableMeterId(
                        Key.of("my.key"),
                        "dollars",
                        Tags.of("key", "value"),
                        Tags.of("meta", "data"))
        );
    }

    @Test
    void defaultIncrementByOne() {
        Counter counter = spy(new Counter() {
            @Override
            public void increment(double amount) {

            }
        });
        counter.increment();
        verify(counter, times(1)).increment(1.0);
    }
}