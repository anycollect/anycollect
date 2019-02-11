package io.github.anycollect.core.impl.scheduler;

import javax.annotation.Nonnull;
import java.util.concurrent.*;

final class CustomFuture<V> implements RunnableScheduledFuture<V> {
    private final RunnableScheduledFuture<V> delegate;
    private volatile long periodInNanos = 0;

    CustomFuture(final RunnableScheduledFuture<V> delegate) {
        this.delegate = delegate;
    }

    void setPeriodInNanos(final long period) {
        this.periodInNanos = period;
    }

    long getPeriodInNanos() {
        return periodInNanos;
    }

    @Override
    public boolean isPeriodic() {
        return delegate.isPeriodic();
    }

    @Override
    public long getDelay(@Nonnull final TimeUnit unit) {
        return delegate.getDelay(unit);
    }

    @Override
    public int compareTo(@Nonnull final Delayed o) {
        return delegate.compareTo(o);
    }

    @Override
    public void run() {
        delegate.run();
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return delegate.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    @Override
    public boolean isDone() {
        return delegate.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return delegate.get();
    }

    @Override
    public V get(final long timeout, @Nonnull final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.get(timeout, unit);
    }
}
