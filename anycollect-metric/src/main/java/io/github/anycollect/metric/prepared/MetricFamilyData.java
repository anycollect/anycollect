package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
final class MetricFamilyData {
    private String key;
    private Tags tags;
    private Tags meta;
}
