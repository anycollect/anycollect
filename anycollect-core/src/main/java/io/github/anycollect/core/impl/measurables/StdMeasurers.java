package io.github.anycollect.core.impl.measurables;

import io.github.anycollect.core.api.measurable.*;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Extension(name = StdMeasurers.NAME, point = Measurers.class)
public final class StdMeasurers implements Measurers {
    private static final Logger LOG = LoggerFactory.getLogger(StdMeasurers.class);
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
    public boolean hasDefinition(@Nonnull final String familyName) {
        return configs.containsKey(familyName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Measurable> Measurer<T> make(@Nonnull final FamilyConfig config) {
        List<MeasurementDefinition> measurements = configs.get(config.getMetricFamilyName());
        if (measurements == null) {
            LOG.error("could not find measurers for {}, available measurers: {}",
                    config.getMetricFamilyName(), configs.keySet());
            throw new IllegalArgumentException("could not find definitions for " + config.getMetricFamilyName());
        }
        return (Measurer<T>) new StdMeasurer(config, measurements);
    }
}
