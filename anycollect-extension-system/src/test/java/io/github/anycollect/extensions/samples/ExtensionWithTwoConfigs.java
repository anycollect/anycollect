package io.github.anycollect.extensions.samples;

import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;

@Extension(contracts = SampleExtensionPoint.class, name = "ExtensionWithTwoConfigs")
public class ExtensionWithTwoConfigs implements SampleExtensionPoint {
    @ExtCreator
    public ExtensionWithTwoConfigs(@ExtConfig SampleExtensionConfig config1,
                                   @ExtConfig SampleExtensionConfig config2) {

    }
}
