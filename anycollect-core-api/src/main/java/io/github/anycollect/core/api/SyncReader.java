package io.github.anycollect.core.api;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;

import javax.annotation.Nonnull;

public interface SyncReader extends Route {
    void read(@Nonnull Dispatcher dispatcher) throws InterruptedException, QueryException, ConnectionException;

    @Nonnull
    default String getTargetId() {
        return getId();
    }

    @Nonnull
    default String getQueryId() {
        return getId();
    }

    int getPeriod();
}
