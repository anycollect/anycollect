package io.github.anycollect.metric;

import java.util.stream.Stream;

public interface Meter {
    MeterId getId();

    Stream<Metric> measure();
}
