package io.github.anycollect.core.impl.measurables;

import io.github.anycollect.core.api.measurable.FamilyConfig;
import io.github.anycollect.core.api.measurable.Measurable;
import io.github.anycollect.core.api.measurable.Measurer;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

// TODO and unit map: milliseconds -> ms!
public final class StdMeasurer<T extends Measurable> implements Measurer<T> {
    private final FamilyConfig config;
    private final List<MeasurementDefinition> measurementDefinitions;

    public StdMeasurer(@Nonnull final FamilyConfig config,
                       @Nonnull final List<MeasurementDefinition> measurements) {
        Objects.requireNonNull(config, "config must not be null");
        Objects.requireNonNull(measurements, "measurement definitions must not be null");
        this.config = config;
        this.measurementDefinitions = measurements;
    }

    @Override
    public Set<String> getPaths() {
        return measurementDefinitions.stream().flatMap(measurement -> measurement.getPaths().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public MetricFamily measure(@Nonnull final T obj, final long timestamp) throws QueryException {
        List<Measurement> measurements = new ArrayList<>();
        for (MeasurementDefinition measurementDefinition : measurementDefinitions) {
            Measurement measurement = resolve(obj, measurementDefinition, config.getBaseUnit());
            measurements.add(measurement);
        }
        ImmutableTags.Builder builder = Tags.builder().concat(config.getTags());
        for (String tagKey : config.getTagKeys()) {
            String tagValue = obj.getTag(tagKey);
            if (tagValue == null) {
                throw new QueryException("could not find tag value for key: \"" + tagKey + "\"");
            }
            builder.tag(tagKey, tagValue);
        }
        return new ImmutableMetricFamily(config.getKey(), timestamp, measurements, builder.build(), config.getMeta());
    }

    private Measurement resolve(final T obj, final MeasurementDefinition config, @Nullable final String baseUnit)
            throws QueryException {
        double value = (double) obj.getValue(config.getPath());

        if (config.isUseBaseUnit()) {
            if (baseUnit != null) {
                return new ImmutableMeasurement(config.getStat(), config.getType(), baseUnit, value);
            } else {
                throw new QueryException("base unit is required");
            }
        }
        if (config.getUnitOf() != null) {
            String unit = obj.getUnit(config.getUnitOf());
            return new ImmutableMeasurement(config.getStat(), config.getType(), unit, value);
        } else {
            throw new QueryException("unitOf is null");
        }
    }
}
