package io.github.anycollect.extensions.samples;

import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;

@Extension(name = "ExtensionPointWithoutAnnotation", point = ExtensionPointWithoutAnnotation.class)
public class ExtensionPointWithoutAnnotaionImpl implements ExtensionPointWithoutAnnotation {
    @ExtCreator
    public ExtensionPointWithoutAnnotaionImpl() {

    }
}
