package io.github.anycollect.testing.jmx;

public interface HistogramMBean {
    long getCount();

    double getMin();

    double getMean();

    double getStdDev();

    double getMax();

    double get50thPercentile();

    double get75thPercentile();

    double get90thPercentile();

    double get95thPercentile();

    double get99thPercentile();

    double get999thPercentile();
}
