package io.github.anycollect.extensions.samples;

import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;

import java.util.ArrayList;
import java.util.List;

public class MultipleDependency {
    @Extension(point = SampleExtensionPoint.class, name = "OneMultipleDependency")
    public static class OneMultipleDependency implements SampleExtensionPoint {
        @ExtCreator
        public OneMultipleDependency(
                @ExtDependency(qualifier = "delegates") List<SampleExtensionPoint> delegates) {
        }
    }

    @Extension(point = SampleExtensionPoint.class, name = "ArrayListDependency")
    public static class ArrayListDependency implements SampleExtensionPoint {
        @ExtCreator
        public ArrayListDependency(
                @ExtDependency(qualifier = "delegates") ArrayList<SampleExtensionPoint> delegates) {
        }
    }
}
