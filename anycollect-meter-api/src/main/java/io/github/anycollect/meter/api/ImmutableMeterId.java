package io.github.anycollect.meter.api;

import io.github.anycollect.metric.Key;
import io.github.anycollect.metric.Tags;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;

@Getter
@ToString(of = {"key", "unit", "tags"}, includeFieldNames = false)
@EqualsAndHashCode(of = {"key", "unit", "tags"})
final class ImmutableMeterId implements MeterId {
    private final Key key;
    private final String unit;
    private final Tags tags;
    private final Tags meta;

    ImmutableMeterId(@Nonnull final Key key, @Nonnull final String unit,
                     @Nonnull final Tags tags, @Nonnull final Tags meta) {
        this.key = key;
        this.unit = unit;
        this.tags = tags;
        this.meta = meta;
    }
}
