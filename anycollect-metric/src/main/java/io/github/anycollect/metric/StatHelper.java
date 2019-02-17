package io.github.anycollect.metric;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class StatHelper {
    private static final Pattern MAX_VALUE_PATTERN = Pattern.compile("max|upper");
    private static final Pattern MIN_VALUE_PATTERN = Pattern.compile("min|lower_[0-9]+");
    private static final Pattern STD_VALUE_PATTERN = Pattern.compile("std");
    private static final Pattern MEAN_VALUE_PATTERN = Pattern.compile("mean");
    private static final Pattern PERCENTILE_VALUE_PATTERN = Pattern.compile("(max|upper|mean)_([0-9]+)");
    private static final Pattern LE_BUCKET_VALUE_PATTERN = Pattern.compile("le_(\\d+(\\.\\d+)*|Infinity)");
    private static final Pattern VALUE_PATTERN = Pattern.compile("value");

    private StatHelper() {
    }

    static Stat parse(final String stat) {
        Objects.requireNonNull(stat, "tag value must not be null");
        if (MIN_VALUE_PATTERN.matcher(stat).matches()) {
            return Stat.min();
        }
        if (MAX_VALUE_PATTERN.matcher(stat).matches()) {
            return Stat.max();
        }
        if (MEAN_VALUE_PATTERN.matcher(stat).matches()) {
            return Stat.mean();
        }
        if (STD_VALUE_PATTERN.matcher(stat).matches()) {
            return Stat.std();
        }
        if (VALUE_PATTERN.matcher(stat).matches()) {
            return Stat.value();
        }
        Matcher matcher = PERCENTILE_VALUE_PATTERN.matcher(stat);
        if (matcher.matches()) {
            Stat subStat = parse(matcher.group(1));
            int num = Integer.parseInt(matcher.group(2));
            return Stat.percentile(subStat, num);
        }
        matcher = LE_BUCKET_VALUE_PATTERN.matcher(stat);
        if (matcher.matches()) {
            return LeBucket.of(Double.parseDouble(matcher.group(1)));
        }
        throw new IllegalArgumentException("unrecognized stat: " + stat);
    }
}
