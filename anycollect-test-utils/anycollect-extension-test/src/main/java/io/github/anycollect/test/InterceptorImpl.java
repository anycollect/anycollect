package io.github.anycollect.test;

import io.github.anycollect.assertj.AnyCollectAssertions;
import io.github.anycollect.assertj.MetricAssert;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.annotations.InstanceId;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.awaitility.Awaitility.await;

@Extension(name = InterceptorImpl.NAME, point = Interceptor.class)
public final class InterceptorImpl implements Interceptor {
    public static final String NAME = "Interceptor";
    private final String id;
    private final Object lock = new Object();
    @GuardedBy("lock")
    private final List<Metric> intercepted = new ArrayList<>();
    private final int awaitSeconds = 10;

    @ExtCreator
    public InterceptorImpl(@InstanceId @Nonnull final String id) {
        this.id = id;
    }

    @Override
    public void write(@Nonnull final List<? extends Metric> metrics) {
        synchronized (lock) {
            intercepted.addAll(metrics);
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public MetricAssert intercepted(final String key) {
        return retryIntercepted(() -> {
            synchronized (lock) {
                return AnyCollectAssertions.assertThatMetrics(intercepted)
                        .contains(key);
            }
        });
    }

    @Override
    public MetricAssert intercepted(final String key, final Tags tags) {
        return retryIntercepted(() -> {
            synchronized (lock) {
                return AnyCollectAssertions.assertThatMetrics(intercepted)
                        .contains(key, tags);
            }
        });
    }

    @Override
    public MetricAssert intercepted(final String key, final Tags tags, final Tags meta) {
        return retryIntercepted(() -> {
            synchronized (lock) {
                return AnyCollectAssertions.assertThatMetrics(intercepted)
                        .contains(key, tags, meta);
            }
        });
    }

    private MetricAssert retryIntercepted(final Callable<MetricAssert> callable) {
        CompletableFuture<MetricAssert> future = new CompletableFuture<>();
        await()
                .atMost(awaitSeconds, TimeUnit.SECONDS)
                .ignoreExceptions()
                .until(() -> {
                    MetricAssert metricAssert = callable.call();
                    metricAssert.isNotNull();
                    future.complete(metricAssert);
                    return true;
                });
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            // must never happen
            throw new RuntimeException(e);
        }
    }
}
