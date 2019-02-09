package io.github.anycollect.core.impl;

import io.github.anycollect.core.impl.scheduler.Cancellation;

public class NoopCancelation implements Cancellation {
    @Override
    public void cancel() {

    }
}
