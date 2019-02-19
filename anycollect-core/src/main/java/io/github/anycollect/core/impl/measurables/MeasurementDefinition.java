package io.github.anycollect.core.impl.measurables;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

// TODO use target unit
@Getter
public final class MeasurementDefinition {
    private final String id;
    private final String path;
    private final Stat stat;
    private final Type type;
    private final String unitOf;
    private final boolean useBaseUnit;

    @JsonCreator
    public MeasurementDefinition(@JsonProperty(value = "id", required = true) @Nonnull final String id,
                                 @JsonProperty(value = "path", required = true) @Nonnull final String path,
                                 @JsonProperty(value = "stat", required = true) @Nonnull final Stat stat,
                                 @JsonProperty(value = "type", required = true) @Nonnull final Type type,
                                 @JsonProperty("unitOf") @Nullable final String unitOf,
                                 @JsonProperty(value = "useBaseUnit", required = true) final boolean useBaseUnit) {
        this.id = id;
        this.path = path;
        this.stat = stat;
        this.type = type;
        this.unitOf = unitOf;
        this.useBaseUnit = useBaseUnit;
    }

    public Set<String> getPaths() {
        Set<String> paths = new HashSet<>();
        paths.add(path);
        if (unitOf != null) {
            paths.add(unitOf);
        }
        return paths;
    }
}
