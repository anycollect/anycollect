package io.github.anycollect.metric.frame;

import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public final class ImmutableMeasurementFrame implements MeasurementFrame {
    private final Stat stat;
    private final Type type;
    private final String unit;
}
