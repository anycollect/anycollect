package io.github.anycollect.extensions.samples;

import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.annotations.InstanceId;

@Extension(name = "WithId", contracts = SampleExtensionPoint.class)
public class ExtensionPointWithId implements SampleExtensionPoint {
    private final String id;

    @ExtCreator
    public ExtensionPointWithId(@InstanceId final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
