package io.github.anycollect.core.impl.pull.separate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = RegExpConcurrencyRule.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RegExpConcurrencyRule.class, name = "regexp")})
public interface ConcurrencyRule {
    int getPoolSize(@Nonnull Target<?> target, int fallback);
}
