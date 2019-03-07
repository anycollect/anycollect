package io.github.anycollect.testing.jmx;

public final class Histogram implements HistogramMBean {
    @Override
    public long getCount() {
        return System.currentTimeMillis();
    }

    @Override
    public double getMin() {
        return System.currentTimeMillis();
    }

    @Override
    public double getMean() {
        return System.currentTimeMillis();
    }

    @Override
    public double getStdDev() {
        return System.currentTimeMillis();
    }

    @Override
    public double getMax() {
        return System.currentTimeMillis();
    }

    @Override
    public double get50thPercentile() {
        return System.currentTimeMillis();
    }

    @Override
    public double get75thPercentile() {
        return System.currentTimeMillis();
    }

    @Override
    public double get90thPercentile() {
        return System.currentTimeMillis();
    }

    @Override
    public double get95thPercentile() {
        return System.currentTimeMillis();
    }

    @Override
    public double get99thPercentile() {
        return System.currentTimeMillis();
    }

    @Override
    public double get999thPercentile() {
        return System.currentTimeMillis();
    }
}
