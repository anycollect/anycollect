package io.github.anycollect.extensions.samples;

import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;

import java.util.List;

public class Configs {
    @Extension(name = "ListOfStrings", contracts = SampleExtensionPoint.class)
    public static class ListOfStrings implements SampleExtensionPoint {
        private final List<String> config;

        @ExtCreator
        public ListOfStrings(@ExtConfig List<String> config) {
            this.config = config;
        }

        public List<String> getConfig() {
            return config;
        }
    }

    @Extension(name = "ListOfStringsWithKey", contracts = SampleExtensionPoint.class)
    public static class ListOfStringsWithKey implements SampleExtensionPoint {
        private final List<String> aliases;

        @ExtCreator
        public ListOfStringsWithKey(@ExtConfig(key = "aliases") List<String> aliases) {
            this.aliases = aliases;
        }

        public List<String> getAliases() {
            return aliases;
        }
    }
}
