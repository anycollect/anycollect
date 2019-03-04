package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
final class MeasurementFrame {
    private final Stat stat;
    private final Type type;
    private final String unit;
}
