package io.github.anycollect.core.api.internal;

public interface Clock {
    Clock DEFAULT = new Clock() {
        @Override
        public long wallTime() {
            return System.currentTimeMillis();
        }

        @Override
        public long monotonicTime() {
            return System.nanoTime();
        }
    };

    long wallTime();

    long monotonicTime();

    static Clock getDefault() {
        return DEFAULT;
    }
}
