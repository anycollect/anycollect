package io.github.anycollect.extensions.samples;

import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;

@Extension(contracts = SampleExtensionPoint.class, name = "SampleWithDependency")
public class SampleExtensionWithDependency implements SampleExtensionPoint {
    @ExtCreator
    public SampleExtensionWithDependency(
            @ExtDependency(qualifier = "delegate") SampleExtensionPoint point,
            @ExtConfig SampleExtensionConfig config) {

    }
}
