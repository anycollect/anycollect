package io.github.anycollect.readers.jmx.utils;

public class HistogramTest implements HistogramTestMBean {
    @Override
    public double getMin() {
        return 1;
    }

    @Override
    public double getMax() {
        return 2;
    }

    @Override
    public double getMean() {
        return 3;
    }

    @Override
    public double getStdDev() {
        return 4;
    }

    @Override
    public double get50thPercentile() {
        return 50;
    }

    @Override
    public double get75thPercentile() {
        return 75;
    }

    @Override
    public double get90thPercentile() {
        return 90;
    }

    @Override
    public double get95thPercentile() {
        return 95;
    }

    @Override
    public double get99thPercentile() {
        return 99;
    }
}
