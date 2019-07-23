package io.github.anycollect.core.impl.scheduler;

import io.github.anycollect.core.api.internal.Cancellation;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public interface Scheduler {
    default Cancellation scheduleAtFixedRate(@Nonnull Runnable runnable, long period, @Nonnull TimeUnit unit) {
        return scheduleAtFixedRate(runnable, 0L, period, unit);
    }

    default Cancellation scheduleAtFixedRate(@Nonnull Runnable runnable, long delay, long period, @Nonnull TimeUnit unit) {
        return scheduleAtFixedRate(runnable, delay, period, unit, false);
    }

    default Cancellation scheduleAtFixedRate(@Nonnull Runnable runnable,
                                             long period,
                                             @Nonnull TimeUnit unit,
                                             boolean allowOverworkAfterPause) {
        return scheduleAtFixedRate(runnable, 0L, period, unit, allowOverworkAfterPause);
    }

    Cancellation scheduleAtFixedRate(@Nonnull Runnable runnable,
                                     long delay,
                                     long period,
                                     @Nonnull TimeUnit unit,
                                     boolean allowOverworkAfterPause);

    void shutdown();

    boolean isShutdown();
}
