package io.github.anycollect.extensions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = PropertyActivation.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PropertyActivation.class, name = "property")
})
public interface Activation {
    static Activation active() {
        return () -> true;
    }

    boolean isReached();
}
