package io.github.anycollect;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public final class Config {
    private final String initFile;
    private final List<String> customFiles;

    @JsonCreator
    public Config(@JsonProperty("init") final String initFile,
                  @JsonProperty("custom") final List<String> customFiles) {
        this.initFile = initFile;
        this.customFiles = customFiles;
    }
}
