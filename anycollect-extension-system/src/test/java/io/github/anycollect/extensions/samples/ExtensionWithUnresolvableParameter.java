package io.github.anycollect.extensions.samples;

import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;

@Extension(contracts = SampleExtensionPoint.class, name = "UnresolvableParameter")
public class ExtensionWithUnresolvableParameter implements SampleExtensionPoint {
    @ExtCreator
    public ExtensionWithUnresolvableParameter(SampleExtensionConfig config) {

    }
}
