package io.github.anycollect;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableExport.class)
@JsonDeserialize(as = ImmutableExport.class)
public interface Export {
    @JsonProperty("file")
    String file();

    @JsonProperty("instances")
    List<String> instances();
}
