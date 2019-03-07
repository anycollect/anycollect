package io.github.anycollect.testing.jmx;

public final class Gauge implements GaugeMBean {
    @Override
    public long getLongValue() {
        return System.currentTimeMillis();

    }

    @Override
    public double getDoubleValue() {
        return System.currentTimeMillis();
    }
}
