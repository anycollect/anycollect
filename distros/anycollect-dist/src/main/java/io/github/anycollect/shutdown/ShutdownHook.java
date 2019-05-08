package io.github.anycollect.shutdown;

import java.util.List;

public final class ShutdownHook extends Thread {
    private final List<ShutdownTask> tasks;

    public ShutdownHook(final List<ShutdownTask> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName("graceful-shutdown");
            for (ShutdownTask task : tasks) {
                task.shutdown();
            }
        } finally {
            Thread.currentThread().setName(name);
        }
    }
}
