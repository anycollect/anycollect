package io.github.anycollect.shutdown;

import java.util.List;

public final class ShutdownHook extends Thread {
    private final List<ShutdownTask> tasks;

    public ShutdownHook(final List<ShutdownTask> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void run() {
        for (ShutdownTask task : tasks) {
            task.shutdown();
        }
    }
}
