package io.github.anycollect.core.impl.router.adapters;

import io.github.anycollect.core.api.SyncReader;
import io.github.anycollect.core.api.dispatcher.Accumulator;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.HealthCheckConfig;
import io.github.anycollect.core.api.internal.PullManager;
import io.github.anycollect.core.api.internal.QueryMatcherResolver;
import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.query.AbstractQuery;
import io.github.anycollect.core.api.query.QueryProvider;
import io.github.anycollect.core.api.target.AbstractTarget;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.core.impl.router.MetricProducer;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import java.util.List;

public final class SyncReaderAdapter implements MetricProducer {
    private final SyncReader reader;
    private final PullManager pullManager;

    public SyncReaderAdapter(@Nonnull final SyncReader reader, @Nonnull final PullManager pullManager) {
        this.reader = reader;
        this.pullManager = pullManager;
    }

    @Override
    public void start(@Nonnull final Dispatcher dispatcher) {
        pullManager.start(
                ServiceDiscovery.singleton(new SyncReaderToTargetAdapter(reader)),
                QueryProvider.singleton(new SyncReaderToQueryAdapter(reader)),
                QueryMatcherResolver.alwaysAll(reader.getPeriod()),
                dispatcher,
                HealthCheckConfig.builder().tags(Tags.of("check", reader.getId())).build());
    }

    @Nonnull
    @Override
    public String getAddress() {
        return reader.getId();
    }

    private static final class SyncReaderToTargetAdapter extends AbstractTarget {
        SyncReaderToTargetAdapter(@Nonnull final SyncReader reader) {
            super(reader.getTargetId(), Tags.empty(), Tags.empty());
        }
    }

    private static final class SyncReaderToQueryAdapter extends AbstractQuery<SyncReaderToTargetAdapter> {
        private final SyncReader reader;

        SyncReaderToQueryAdapter(@Nonnull final SyncReader reader) {
            super(reader.getQueryId());
            this.reader = reader;
        }

        @Nonnull
        @Override
        public Job bind(@Nonnull final SyncReaderToTargetAdapter target) {
            return new SyncReaderToJobAdapter(reader);
        }
    }

    private static final class SyncReaderToJobAdapter implements Job {
        private final SyncReader reader;

        private SyncReaderToJobAdapter(final SyncReader reader) {
            this.reader = reader;
        }

        @Override
        public List<Metric> execute() throws InterruptedException, QueryException, ConnectionException {
            Accumulator accumulator = Dispatcher.accumulator();
            reader.read(accumulator);
            return accumulator.purge();
        }
    }
}
