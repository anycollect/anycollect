package io.github.anycollect.metric;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class CounterTest {
    @Test
    void registryCounter() {
        MeterRegistry registry = mock(MeterRegistry.class);
        Counter.key("my.key")
                .unit("dollars")
                .tag("key", "value")
                .meta("meta", "data")
                .register(registry);
        verify(registry, times(1)).counter(MeterId.key("my.key").unit("dollars").tag("key", "value").meta("meta", "data").build());
    }

    @Test
    void defaultIncrementByOne() {
        Counter counter = spy(new Counter() {
            @Override
            public void increment(double amount) {

            }

            @Override
            public MeterId getId() {
                return null;
            }

            @Override
            public Stream<Metric> measure() {
                return null;
            }
        });
        counter.increment();
        verify(counter, times(1)).increment(1.0);
    }
}