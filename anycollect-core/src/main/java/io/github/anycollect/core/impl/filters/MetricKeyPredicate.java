package io.github.anycollect.core.impl.filters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class MetricKeyPredicate implements Predicate<Metric> {
    private final Pattern pattern;
    private final String equals;
    private final String startsWith;
    private final String endsWith;
    private final String contains;

    @JsonCreator
    public MetricKeyPredicate(@JsonProperty("regexp") @Nullable final String regexp,
                              @JsonProperty("equals") @Nullable final String equals,
                              @JsonProperty("starts") @Nullable final String startsWith,
                              @JsonProperty("ends") @Nullable final String endsWith,
                              @JsonProperty("contains") @Nullable final String contains) {
        if (regexp != null) {
            this.pattern = Pattern.compile(regexp);
        } else {
            this.pattern = null;
        }
        this.equals = equals;
        this.startsWith = startsWith;
        this.endsWith = endsWith;
        this.contains = contains;
    }

    @Override
    public boolean test(final Metric frame) {
        if (equals != null && !frame.getKey().equals(equals)) {
            return false;
        }
        if (startsWith != null && !frame.getKey().normalize().startsWith(startsWith)) {
            return false;
        }
        if (endsWith != null && !frame.getKey().normalize().endsWith(endsWith)) {
            return false;
        }
        if (contains != null && !frame.getKey().normalize().contains(contains)) {
            return false;
        }
        if (pattern != null) {
            return pattern.matcher(frame.getKey()).matches();
        }
        return true;
    }
}
