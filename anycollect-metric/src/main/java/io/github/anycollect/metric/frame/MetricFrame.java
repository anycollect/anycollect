package io.github.anycollect.metric.frame;

import io.github.anycollect.metric.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public final class MetricFrame {
    private final String key;
    private final Tags tags;
    private final Tags meta;
    private final List<MeasurementFrame> measurements;
}
