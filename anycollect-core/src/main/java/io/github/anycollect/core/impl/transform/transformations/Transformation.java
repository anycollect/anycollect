package io.github.anycollect.core.impl.transform.transformations;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "transform")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TagTransformation.class, name = "tag"),
})
public interface Transformation {
    @Nonnull
    Sample transform(@Nonnull Sample source);
}
