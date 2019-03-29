package io.github.anycollect.metric.frame;

import io.github.anycollect.metric.Tags;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public final class ImmutableMetricFrame implements MetricFrame {
    private final String key;
    private final Tags tags;
    private final Tags meta;
}
