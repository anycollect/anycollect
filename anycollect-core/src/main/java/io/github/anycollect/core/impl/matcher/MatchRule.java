package io.github.anycollect.core.impl.matcher;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = RegExpMatchRule.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RegExpMatchRule.class, name = "regexp")})
public interface MatchRule {
    boolean match(@Nonnull Target target, @Nonnull Query query);

    int getPeriod();
}
