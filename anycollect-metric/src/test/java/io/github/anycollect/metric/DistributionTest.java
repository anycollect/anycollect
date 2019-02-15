package io.github.anycollect.metric;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
                .distribution(MeterId.key("my.distribution").unit("ms").tag("key", "val").meta("m", "d").build());
    }
}