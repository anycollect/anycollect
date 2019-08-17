package io.github.anycollect.meter.api;

import io.github.anycollect.metric.Key;
import io.github.anycollect.metric.Tags;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class DistributionTest {
    @Test
    void registryDistribution() {
        MeterRegistry registry = mock(MeterRegistry.class);
        Distribution.key("my.distribution")
                .unit("ms")
                .tag("key", "val")
                .meta("m", "d")
                .register(registry);
        verify(registry, times(1))
                .distribution(
                        new ImmutableMeterId(
                                Key.of("my.distribution"),
                                "ms",
                                Tags.of("key", "val"),
                                Tags.of("m", "d")));
    }
}