package io.github.anycollect.readers.jmx.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@Getter
@ToString
public final class MeasurementPath {
    private final List<String> valuePath;
    private final Stat stat;
    private final Type type;
    private final String name;
    private final String unit;

    @JsonCreator
    public MeasurementPath(@JsonProperty(value = "path", required = true) @Nonnull final String valuePath,
                           @JsonProperty(value = "stat", required = true) @Nonnull final Stat stat,
                           @JsonProperty(value = "type", required = true) @Nonnull final Type type,
                           @JsonProperty(value = "unit", required = false) @Nullable final String unit) {
        this.valuePath = Arrays.asList(valuePath.split("\\."));
        this.stat = stat;
        this.type = type;
        this.name = this.valuePath.get(0);
        this.unit = unit != null ? unit : "";
    }

    public String getAttribute() {
        return name;
    }
}
