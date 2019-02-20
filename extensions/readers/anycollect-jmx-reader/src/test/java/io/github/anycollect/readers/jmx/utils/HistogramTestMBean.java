package io.github.anycollect.readers.jmx.utils;

public interface HistogramTestMBean {
    double getMin();

    double getMax();

    double getMean();

    double getStdDev();

    double get50thPercentile();

    double get75thPercentile();

    double get90thPercentile();

    double get95thPercentile();

    double get99thPercentile();
}
