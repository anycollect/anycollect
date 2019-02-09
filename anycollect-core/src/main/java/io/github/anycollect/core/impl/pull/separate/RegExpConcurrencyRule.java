package io.github.anycollect.core.impl.pull.separate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.target.Target;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

@ToString
public final class RegExpConcurrencyRule implements ConcurrencyRule {
    private final Pattern labelPattern;
    private final int poolSize;

    @JsonCreator
    public RegExpConcurrencyRule(@Nonnull @JsonProperty("label") final String label,
                                 @JsonProperty("poolSize") final int poolSize) {
        this.labelPattern = Pattern.compile(label);
        this.poolSize = poolSize;
    }

    @Override
    public int getPoolSize(@Nonnull final Target<?> target, final int fallback) {
        if (labelPattern.matcher(target.getLabel()).matches()) {
            return poolSize;
        }
        return fallback;
    }
}
