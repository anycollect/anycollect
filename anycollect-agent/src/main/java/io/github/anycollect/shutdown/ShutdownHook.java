package io.github.anycollect.shutdown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class ShutdownHook extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(ShutdownHook.class);
    private final List<ShutdownTask> tasks;

    public ShutdownHook(final List<ShutdownTask> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName("graceful-shutdown");
            LOG.info("Start graceful shutdown");
            for (ShutdownTask task : tasks) {
                task.shutdown();
            }
        } finally {
            Thread.currentThread().setName(name);
        }
        LOG.info("Graceful shutdown has been successfully completed");
    }
}
