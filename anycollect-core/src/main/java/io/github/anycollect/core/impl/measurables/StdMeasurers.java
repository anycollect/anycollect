package io.github.anycollect.core.impl.measurables;

import io.github.anycollect.core.api.measurable.*;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Extension(name = StdMeasurers.NAME, point = Measurers.class)
public final class StdMeasurers implements Measurers {
    public static final String NAME = "Measurers";
    private final Map<String, List<MeasurementDefinition>> configs;

    @ExtCreator
    public StdMeasurers(@ExtConfig @Nonnull final MeasurersConfig config) {
        configs = new HashMap<>();
        for (MetricFamilyDefinition measurable : config.getMeasurables()) {
            configs.put(measurable.getName(), measurable.getMeasurements());
        }
    }

    @Override
    public <T extends Measurable> Measurer<T> make(@Nonnull final FamilyConfig config) {
        List<MeasurementDefinition> measurements = configs.get(config.getMetricFamilyName());
        if (measurements == null) {
            // TODO
            throw new RuntimeException("could not find definitions for " + config.getMetricFamilyName());
        }
        return new StdMeasurer<>(config, measurements);
    }
}
