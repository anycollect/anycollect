package io.github.anycollect.extensions.substitution;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public interface VarSubstitutor {
    VarSubstitutor EMPTY = new VarSubstitutor() {
        @Override
        @Nullable
        public String substitute(@Nonnull final String varName) {
            return null;
        }
    };

    static VarSubstitutor firstNonNull(VarSubstitutor... substitutors) {
        return new FirstNonNullVarSubstitutor(substitutors);
    }

    static VarSubstitutor of(String... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("size must be event");
        }
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < keyValues.length / 2; i++) {
            map.put(keyValues[2 * i], keyValues[2 * i + 1]);
        }
        return new MapVarSubstitutor(map);
    }

    static VarSubstitutor ofMap(Map<String, String> map) {
        return new MapVarSubstitutor(map);
    }

    static VarSubstitutor env() {
        return new EnvVarSubstitutor();
    }

    static VarSubstitutor ofClassPathFile(String propertyFileName) throws IOException {
        Properties properties = new Properties();
        URL resource = VarSubstitutor.class.getClassLoader().getResource(propertyFileName);
        if (resource == null) {
            throw new FileNotFoundException("resource " + propertyFileName + " not found in classpath");
        }
        properties.load(resource.openStream());
        return new PropertiesVarSubstitutor(properties);
    }

    @Nullable
    String substitute(@Nonnull String varName);
}
