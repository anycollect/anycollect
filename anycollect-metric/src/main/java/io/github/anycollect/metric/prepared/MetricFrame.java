package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
final class MetricFrame {
    private final String key;
    private final Tags tags;
    private final Tags meta;
}
