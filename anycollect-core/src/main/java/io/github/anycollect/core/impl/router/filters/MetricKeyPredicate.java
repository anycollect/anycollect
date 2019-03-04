package io.github.anycollect.core.impl.router.filters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class MetricKeyPredicate implements Predicate<Metric> {
    private final Pattern pattern;
    private final String startsWith;
    private final String endsWith;
    private final String contains;

    @JsonCreator
    public MetricKeyPredicate(@JsonProperty("regexp") @Nullable final String regexp,
                              @JsonProperty("starts") @Nullable final String startsWith,
                              @JsonProperty("ends") @Nullable final String endsWith,
                              @JsonProperty("contains") @Nullable final String contains) {
        if (regexp != null) {
            this.pattern = Pattern.compile(regexp);
        } else {
            this.pattern = null;
        }
        this.startsWith = startsWith;
        this.endsWith = endsWith;
        this.contains = contains;
    }

    @Override
    public boolean test(final Metric metric) {
        if (startsWith != null && !metric.getKey().startsWith(startsWith)) {
            return false;
        }
        if (endsWith != null && !metric.getKey().endsWith(endsWith)) {
            return false;
        }
        if (contains != null && !metric.getKey().contains(contains)) {
            return false;
        }
        if (pattern != null) {
            return pattern.matcher(metric.getKey()).matches();
        }
        return true;
    }
}
