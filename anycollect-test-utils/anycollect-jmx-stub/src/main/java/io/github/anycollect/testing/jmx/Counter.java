package io.github.anycollect.testing.jmx;

public final class Counter implements CounterMBean {
    @Override
    public double getDoubleCount() {
        return System.currentTimeMillis();
    }

    @Override
    public double getLongCount() {
        return System.currentTimeMillis();
    }
}
