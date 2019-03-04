package io.github.anycollect.core.impl.measurables;

import io.github.anycollect.core.api.measurable.FamilyConfig;
import io.github.anycollect.core.api.measurable.Measurable;
import io.github.anycollect.core.api.measurable.Measurer;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

// TODO and unit map: milliseconds -> ms!
public final class StdMeasurer implements Measurer<Measurable> {
    private final FamilyConfig config;
    private final List<MeasurementDefinition> measurementDefinitions;

    public StdMeasurer(@Nonnull final FamilyConfig config,
                       @Nonnull final List<MeasurementDefinition> measurements) {
        Objects.requireNonNull(config, "config must not be null");
        Objects.requireNonNull(measurements, "measurement definitions must not be null");
        this.config = config;
        this.measurementDefinitions = measurements;
    }

    @Nonnull
    @Override
    public Set<String> getPaths() {
        return measurementDefinitions.stream().flatMap(measurement -> measurement.getPaths().stream())
                .collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public Metric measure(@Nonnull final Measurable measurable, final long timestamp) throws QueryException {
        Objects.requireNonNull(measurable, "measurable must not be null");
        List<Measurement> measurements = new ArrayList<>();
        for (MeasurementDefinition measurementDefinition : measurementDefinitions) {
            Measurement measurement = resolve(measurable, measurementDefinition, config.getBaseUnit());
            measurements.add(measurement);
        }
        ImmutableTags.Builder builder = Tags.builder().concat(config.getTags()).concat(measurable.getTags());
        for (String tagKey : config.getTagKeys()) {
            String tagValue = measurable.getTag(tagKey);
            if (tagValue == null) {
                throw new QueryException("could not find tag value for key: \"" + tagKey + "\"");
            }
            builder.tag(tagKey, tagValue);
        }
        return new ImmutableMetric(config.getKey(), timestamp, measurements, builder.build(), config.getMeta());
    }

    private Measurement resolve(@Nonnull final Measurable obj,
                                @Nonnull final MeasurementDefinition config,
                                @Nullable final String baseUnit)
            throws QueryException {
        Object objValue = obj.getValue(config.getPath());
        if (objValue == null) {
            throw new QueryException("could not find value for path: " + config.getPath());
        }
        if (!(objValue instanceof Number)) {
            throw new QueryException("value returned for path: " + config.getPath()
                    + " has type " + objValue.getClass());
        }
        double value = ((Number) objValue).doubleValue();
        if (config.getUnitOf() == null) {
            return new ImmutableMeasurement(config.getStat(), config.getType(), baseUnit, value);
        }
        String unit = obj.getUnit(config.getUnitOf());
        if (unit == null) {
            throw new QueryException("could not find unit for path: " + config.getUnitOf());
        }
        return new ImmutableMeasurement(config.getStat(), config.getType(), unit, value);
    }
}
