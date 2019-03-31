package io.github.anycollect.core.impl.transform.transformations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Value {
    private final Pattern sourcePattern;
    private final String target;

    @JsonCreator
    public Value(@JsonProperty("source") @Nonnull final String source,
                 @JsonProperty("target") @Nonnull final String target) {
        this.sourcePattern = Pattern.compile(source);
        this.target = target;
    }

    @Nullable
    public String transform(@Nonnull final String source) {
        Matcher matcher = sourcePattern.matcher(source);
        if (!matcher.matches()) {
            return null;
        }
        return matcher.replaceAll(target);
    }
}
