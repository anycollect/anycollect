package io.github.anycollect.extensions.samples;

import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;

@Extension(contracts = SampleExtensionPoint.class, name = "WithoutConfig")
public class SampleExtensionWithoutConfig implements SampleExtensionPoint{
    @ExtCreator
    public SampleExtensionWithoutConfig() {

    }
}
