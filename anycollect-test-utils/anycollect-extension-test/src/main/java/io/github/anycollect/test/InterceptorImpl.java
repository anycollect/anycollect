package io.github.anycollect.test;

import io.github.anycollect.assertj.AnyCollectAssertions;
import io.github.anycollect.assertj.SampleAssert;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.annotations.InstanceId;
import io.github.anycollect.metric.Sample;
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
    private final List<Sample> intercepted = new ArrayList<>();
    private final int awaitSeconds = 10;

    @ExtCreator
    public InterceptorImpl(@InstanceId @Nonnull final String id) {
        this.id = id;
    }

    @Override
    public void write(@Nonnull final List<? extends Sample> metrics) {
        synchronized (lock) {
            intercepted.addAll(metrics);
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SampleAssert intercepted(final String key) {
        return retryIntercepted(() -> {
            synchronized (lock) {
                return AnyCollectAssertions.assertThatSamples(intercepted)
                        .contains(key);
            }
        });
    }

    @Override
    public SampleAssert intercepted(final String key, final Tags tags) {
        return retryIntercepted(() -> {
            synchronized (lock) {
                return AnyCollectAssertions.assertThatSamples(intercepted)
                        .contains(key, tags);
            }
        });
    }

    @Override
    public SampleAssert intercepted(final String key, final Tags tags, final Tags meta) {
        return retryIntercepted(() -> {
            synchronized (lock) {
                return AnyCollectAssertions.assertThatSamples(intercepted)
                        .contains(key, tags, meta);
            }
        });
    }

    private SampleAssert retryIntercepted(final Callable<SampleAssert> callable) {
        CompletableFuture<SampleAssert> future = new CompletableFuture<>();
        await()
                .atMost(awaitSeconds, TimeUnit.SECONDS)
                .ignoreExceptions()
                .until(() -> {
                    SampleAssert sampleAssert = callable.call();
                    sampleAssert.isNotNull();
                    future.complete(sampleAssert);
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
