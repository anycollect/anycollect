package io.github.anycollect.testing.jmx;

public final class Histogram implements HistogramMBean {
    private final long delay;

    public Histogram(final long delay) {
        this.delay = delay;
    }

    @Override
    public long getCount() {
        Delay.delay(delay);
        return System.currentTimeMillis();
    }

    @Override
    public double getMin() {
        Delay.delay(delay);
        return System.currentTimeMillis();
    }

    @Override
    public double getMean() {
        Delay.delay(delay);
        return System.currentTimeMillis();
    }

    @Override
    public double getStdDev() {
        Delay.delay(delay);
        return System.currentTimeMillis();
    }

    @Override
    public double getMax() {
        Delay.delay(delay);
        return System.currentTimeMillis();
    }

    @Override
    public double get50thPercentile() {
        Delay.delay(delay);
        return System.currentTimeMillis();
    }

    @Override
    public double get75thPercentile() {
        Delay.delay(delay);
        return System.currentTimeMillis();
    }

    @Override
    public double get90thPercentile() {
        Delay.delay(delay);
        return System.currentTimeMillis();
    }

    @Override
    public double get95thPercentile() {
        Delay.delay(delay);
        return System.currentTimeMillis();
    }

    @Override
    public double get99thPercentile() {
        Delay.delay(delay);
        return System.currentTimeMillis();
    }

    @Override
    public double get999thPercentile() {
        Delay.delay(delay);
        return System.currentTimeMillis();
    }
}
