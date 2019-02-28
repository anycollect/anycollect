package io.github.anycollect.metric;

import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AbstractMeterTest {
    @Test
    void getIdTest() {
        MeterId id = mock(MeterId.class);
        AbstractMeter meter = new AbstractMeter(id) {
            @Nonnull
            @Override
            public MetricFamily measure() {
                return null;
            }
        };
        assertThat(meter.getId()).isSameAs(id);
    }
}