package io.github.anycollect.readers.jmx.monitoring;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;

class MonitoringConstantsTest {
    @Test
    void mustNotBeInstantiated() {
        InvocationTargetException ex = Assertions.assertThrows(InvocationTargetException.class, () -> {
            Constructor<MonitoringConstants> constructor = MonitoringConstants.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
        assertThat(ex.getCause()).isInstanceOf(UnsupportedOperationException.class).hasMessageContaining("utility class");
    }
}