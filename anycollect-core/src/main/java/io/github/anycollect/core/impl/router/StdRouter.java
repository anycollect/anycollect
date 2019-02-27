package io.github.anycollect.core.impl.router;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.Router;
import io.github.anycollect.core.api.Writer;
import io.github.anycollect.core.impl.router.adapters.ReaderAdapter;
import io.github.anycollect.core.impl.router.adapters.WriterAdapter;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.*;

@Extension(name = StdRouter.NAME, point = Router.class)
public class StdRouter implements Router, Lifecycle {
    public static final String NAME = "Router";
    private static final Logger LOG = LoggerFactory.getLogger(StdRouter.class);
    private final ExecutorService executor;
    private final Channel channel;

    @ExtCreator
    public StdRouter(@ExtDependency(qualifier = "reader") @Nonnull final Reader reader,
                     @ExtDependency(qualifier = "writer") @Nonnull final Writer writer,
                     @ExtConfig final RouterConfig config) {
        int threads = config.getPoolSize();
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("anycollect-router-[%d]").build();
        this.executor = new ThreadPoolExecutor(threads, threads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                threadFactory);
        this.channel = new Channel(
                new ReaderAdapter(reader),
                new AsyncDispatcher(executor, new WriterAdapter(writer), config.getClock())
        );
    }

    @Override
    public void init() {
        LOG.info("{} has been successfully initialised", NAME);
    }

    @Override
    public void start() {
        channel.connect();
    }

    @Override
    public void destroy() {
        channel.disconnect();
        executor.shutdownNow();
        LOG.info("{} has been successfully destroyed", NAME);
    }
}
