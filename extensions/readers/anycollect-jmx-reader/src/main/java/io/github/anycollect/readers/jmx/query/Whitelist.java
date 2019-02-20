package io.github.anycollect.readers.jmx.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.management.ObjectName;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public final class Whitelist implements Restriction {
    private final List<Map<String, Pattern>> allowedValuesList;

    @JsonCreator
    public Whitelist(@JsonProperty("keys") @Nonnull final List<String> keys,
                     @JsonProperty("values") @Nonnull final List<List<String>> allowedValuesList) {
        int size = allowedValuesList.size();
        this.allowedValuesList = IntStream.range(0, size)
                .mapToObj(i -> new HashMap<String, Pattern>())
                .collect(toList());
        for (int i = 0; i < keys.size(); ++i) {
            String key = keys.get(i);
            for (int j = 0; j < allowedValuesList.size(); ++j) {
                List<String> allowedValues = allowedValuesList.get(j);
                this.allowedValuesList.get(j).put(key, Pattern.compile(allowedValues.get(i)));
            }
        }
    }

    @Override
    public boolean allows(@Nonnull final ObjectName objectName) {
        Hashtable<String, String> properties = objectName.getKeyPropertyList();
        for (Map<String, Pattern> allowedValues : allowedValuesList) {
            boolean accepted = true;
            for (Map.Entry<String, Pattern> entry : allowedValues.entrySet()) {
                String key = entry.getKey();
                Pattern allowedValue = entry.getValue();
                String actualValue = properties.get(key);
                if (!allowedValue.matcher(actualValue).matches()) {
                    accepted = false;
                }
            }
            if (accepted) {
                return true;
            }
        }
        return false;
    }
}
