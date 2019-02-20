package io.github.anycollect.core.api.measurable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.metric.Tags;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
public final class FamilyConfig {
    private final String key;
    private final String baseUnit;
    private final Set<String> tagKeys;
    private final Tags tags;
    private final Tags meta;
    private final String metricFamilyName;

    @JsonCreator
    public FamilyConfig(@JsonProperty(value = "what", required = true) @Nonnull final String key,
                        @JsonProperty("baseUnit") @Nullable final String baseUnit,
                        @JsonProperty("tagKeys") @Nullable final Set<String> tagKeys,
                        @JsonProperty("tags") @Nullable final Tags tags,
                        @JsonProperty("meta") @Nullable final Tags meta,
                        @JsonProperty(value = "name", required = true) @Nonnull final String metricFamilyName) {
        this.key = key;
        this.tagKeys = tagKeys != null ? new HashSet<>(tagKeys) : Collections.emptySet();
        this.tags = tags != null ? tags : Tags.empty();
        this.meta = meta != null ? meta : Tags.empty();
        this.baseUnit = baseUnit != null ? baseUnit : "";
        this.metricFamilyName = metricFamilyName;
    }
}
