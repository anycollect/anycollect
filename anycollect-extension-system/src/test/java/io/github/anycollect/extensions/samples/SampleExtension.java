package io.github.anycollect.extensions.samples;

import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;

@Extension(name = "Sample", contracts = SampleExtensionPoint.class)
public class SampleExtension implements SampleExtensionPoint {
    @ExtCreator
    public SampleExtension(@ExtConfig SampleExtensionConfig config) {
    }
}
