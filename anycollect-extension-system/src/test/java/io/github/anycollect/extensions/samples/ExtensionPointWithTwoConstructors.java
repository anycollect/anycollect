package io.github.anycollect.extensions.samples;

import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;

@Extension(point = SampleExtensionPoint.class, name = "TwoConstructors")
public class ExtensionPointWithTwoConstructors implements SampleExtensionPoint {
    @ExtCreator
    public ExtensionPointWithTwoConstructors() {

    }

    @ExtCreator
    public ExtensionPointWithTwoConstructors(@ExtConfig SampleExtensionConfig config) {

    }
}
