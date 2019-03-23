package io.github.anycollect.core.api.measurable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;

@Getter
@ToString
public final class MeasurementPath {
    private final String valuePath;
    private final Stat stat;
    private final Type type;

    @JsonCreator
    public MeasurementPath(@JsonProperty(value = "path", required = true) @Nonnull final String valuePath,
                           @JsonProperty(value = "stat", required = true) @Nonnull final Stat stat,
                           @JsonProperty(value = "type", required = true) @Nonnull final Type type) {
        this.valuePath = valuePath;
        this.stat = stat;
        this.type = type;
    }
}
