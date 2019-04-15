package io.github.anycollect.testing.jmx;

public final class Delay {
    private Delay() {
    }

    public static void delay(final long millis) {
        if (millis == 0) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
