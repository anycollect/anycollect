package io.github.anycollect.testing.jmx;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@JsonSerialize(as = ImmutableJmxRegistration.class)
@JsonDeserialize(as = ImmutableJmxRegistration.class)
@Value.Immutable
public interface JmxRegistration {
    static ImmutableJmxRegistration.Builder builder() {
        return ImmutableJmxRegistration.builder();
    }

    String serviceId();

    String host();

    String port();
}
