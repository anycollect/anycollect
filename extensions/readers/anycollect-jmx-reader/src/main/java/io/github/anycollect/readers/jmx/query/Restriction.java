package io.github.anycollect.readers.jmx.query;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.annotation.Nonnull;
import javax.management.ObjectName;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = Whitelist.class)
@JsonSubTypes(
        @JsonSubTypes.Type(value = Whitelist.class, name = "whitelist"))
public interface Restriction {
    Restriction ALL = objectName -> true;

    static Restriction all() {
        return ALL;
    }

    boolean allows(@Nonnull ObjectName objectName);
}
