package io.github.anycollect.shutdown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class RemoveFileShutdownTask implements ShutdownTask {
    private static final Logger LOG = LoggerFactory.getLogger(RemoveFileShutdownTask.class);
    private final Path path;

    public RemoveFileShutdownTask(final Path path) {
        this.path = path;
    }

    @Override
    public void shutdown() {
        try {
            Files.deleteIfExists(path);
            LOG.info("file {} has been successfully deleted", path);
        } catch (IOException e) {
            LOG.warn("cannot remove {}", path);
        }
    }
}
