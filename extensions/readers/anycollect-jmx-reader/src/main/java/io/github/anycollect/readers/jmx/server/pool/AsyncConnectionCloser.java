package io.github.anycollect.readers.jmx.server.pool;

import io.github.anycollect.readers.jmx.server.JmxConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class AsyncConnectionCloser {
    private final ExecutorService asyncCloserService;

    public AsyncConnectionCloser(@Nonnull final ExecutorService asyncCloserService) {
        Objects.requireNonNull(asyncCloserService, "async closer service must not be null");
        this.asyncCloserService = asyncCloserService;
    }

    public final void closeAsync(@Nonnull final JmxConnection connection) {
        Objects.requireNonNull(connection, "connection to close must not be null");
        asyncCloserService.submit(new CloseJob(connection));
    }

    private static final class CloseJob implements Runnable {
        private static final Logger LOG = LoggerFactory.getLogger(CloseJob.class);
        private final JmxConnection connectionToClose;

        CloseJob(final JmxConnection connectionToClose) {
            this.connectionToClose = connectionToClose;
        }

        @Override
        public void run() {
            try {
                connectionToClose.close();
            } catch (IOException e) {
                LOG.debug("unable to close connection {}", connectionToClose);
            }
        }
    }
}
