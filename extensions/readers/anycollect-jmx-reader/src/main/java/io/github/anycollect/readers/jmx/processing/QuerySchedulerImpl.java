package io.github.anycollect.readers.jmx.processing;

import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.Server;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

@NotThreadSafe
public final class QuerySchedulerImpl implements QueryScheduler {
    private final ConcurrentMap<JobId, ScheduledFuture<?>> jobs = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledService;
    private final QueryExecutor queryExecutor;
    private final Duration defaultInterval;
    private final Duration initialDelay;

    public QuerySchedulerImpl(@Nonnull final ScheduledExecutorService scheduledService,
                              @Nonnull final QueryExecutor queryExecutor,
                              @Nonnull final Duration initialDelay,
                              @Nonnull final Duration defaultInterval) {
        this.scheduledService = scheduledService;
        this.queryExecutor = queryExecutor;
        this.initialDelay = initialDelay;
        this.defaultInterval = defaultInterval;
    }

    @Override
    public void schedule(@Nonnull final Set<Server> servers, @Nonnull final Set<Query> queries) {
        Map<JobId, ScheduledFuture<?>> newJobs = new HashMap<>();
        for (Server server : servers) {
            for (Query query : queries) {
                if (server.getApplication().getQueryMatcher().matches(query)) {
                    JobId id = new JobId(server, query);
                    if (jobs.containsKey(id)) {
                        newJobs.put(id, jobs.get(id));
                    } else {
                        QuerySubmitJob job = new QuerySubmitJob(query, server, queryExecutor);
                        ScheduledFuture<?> future = scheduledService.scheduleAtFixedRate(job,
                                initialDelay.toMillis(),
                                query.getInterval().orElse(defaultInterval).toMillis(),
                                TimeUnit.MILLISECONDS);
                        newJobs.put(id, future);
                    }
                }
            }
        }
        for (Map.Entry<JobId, ScheduledFuture<?>> entry : jobs.entrySet()) {
            if (!newJobs.containsKey(entry.getKey())) {
                entry.getValue().cancel(false);
            }
        }
        this.jobs.clear();
        this.jobs.putAll(newJobs);
    }

    @EqualsAndHashCode
    private static final class JobId {
        private final Server server;
        private final Query query;

        JobId(final Server server, final Query query) {
            this.server = server;
            this.query = query;
        }
    }
}
