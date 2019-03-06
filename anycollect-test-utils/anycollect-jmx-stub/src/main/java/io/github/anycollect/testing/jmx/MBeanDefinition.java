package io.github.anycollect.testing.jmx;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

@JsonDeserialize(as = ImmutableMBeanDefinition.class)
@Value.Immutable
public interface MBeanDefinition {
    Map<String, List<String>> keys();

    MBeanType type();
}
