package io.github.anycollect.testing.jmx;

public final class Counter implements CounterMBean {
    private final long delay;

    public Counter(final long delay) {
        this.delay = delay;
    }

    @Override
    public double getDoubleCount() {
        Delay.delay(delay);
        return System.currentTimeMillis();
    }

    @Override
    public double getLongCount() {
        Delay.delay(delay);
        return System.currentTimeMillis();
    }
}
