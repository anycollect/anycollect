package io.github.anycollect.testing.jmx;

public final class Gauge implements GaugeMBean {
    private final long delay;

    public Gauge(final long delay) {
        this.delay = delay;
    }

    @Override
    public long getLongValue() {
        Delay.delay(delay);
        return System.currentTimeMillis();
    }

    @Override
    public double getDoubleValue() {
        Delay.delay(delay);
        return System.currentTimeMillis();
    }
}
