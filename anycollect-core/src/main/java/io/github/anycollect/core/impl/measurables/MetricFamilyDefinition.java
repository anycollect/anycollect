package io.github.anycollect.core.impl.measurables;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MetricFamilyDefinition {
    private final String name;
    private final List<MeasurementDefinition> measurements;

    @JsonCreator
    public MetricFamilyDefinition(
            @JsonProperty("name") @Nonnull final String name,
            @JsonProperty("measurements") @Nonnull final List<MeasurementDefinition> measurements) {
        this.name = name;
        this.measurements = new ArrayList<>(measurements);
    }

    public String getName() {
        return name;
    }

    public List<MeasurementDefinition> getMeasurements() {
        return Collections.unmodifiableList(measurements);
    }
}
