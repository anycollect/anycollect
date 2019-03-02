package io.github.anycollect.core.manifest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;

public final class ExtensionManifest {
    private final String className;

    @JsonCreator
    public ExtensionManifest(@JsonProperty("class") @Nonnull final String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
