package io.github.anycollect.core.impl.scheduler;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public interface Scheduler {
    Cancellation scheduleAtFixedRate(@Nonnull Runnable runnable, long period, @Nonnull TimeUnit unit);

    void shutdown();

    boolean isShutdown();
}
