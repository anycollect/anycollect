package io.github.anycollect.core.impl.measurables;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.List;

public final class MeasurersConfig {
    private final List<MetricFamilyDefinition> measurables;

    @JsonCreator
    public MeasurersConfig(@JsonProperty("families") @Nonnull final List<MetricFamilyDefinition> measurables) {
        this.measurables = measurables;
    }

    public List<MetricFamilyDefinition> getMeasurables() {
        return measurables;
    }
}
