package io.github.anycollect.core.manifest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public final class ModuleManifest {
    @Getter
    private final String manifest;
    private final List<ExtensionManifest> extensions;

    @JsonCreator
    public ModuleManifest(@JsonProperty("manifest") @Nonnull final String manifest,
                          @JsonProperty("extensions") @Nonnull final List<ExtensionManifest> extensions) {
        this.manifest = manifest;
        this.extensions = extensions;
    }

    public List<ExtensionManifest> getExtensions() {
        return Collections.unmodifiableList(extensions);
    }
}
