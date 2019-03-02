package io.github.anycollect.core.manifest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

@JsonRootName("manifest")
public final class ModuleManifest {
    private final List<ExtensionManifest> extensions;

    @JsonCreator
    public ModuleManifest(@JsonProperty("extensions") @Nonnull final List<ExtensionManifest> extensions) {
        this.extensions = extensions;
    }

    public List<ExtensionManifest> getExtensions() {
        return Collections.unmodifiableList(extensions);
    }
}
