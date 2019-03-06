package io.github.anycollect.testing.jmx;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;

@JsonDeserialize(as = ImmutableJmxConfig.class)
@Value.Immutable
public interface JmxConfig {
    static ImmutableJmxConfig.Builder builder() {
        return ImmutableJmxConfig.builder();
    }

    static JmxConfig empty() {
        return ImmutableJmxConfig.builder()
                .domain("test")
                .build();
    }

    String domain();

    List<MBeanDefinition> mbeans();
}
