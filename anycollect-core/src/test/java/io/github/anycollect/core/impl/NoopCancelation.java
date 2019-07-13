package io.github.anycollect.core.impl;

import io.github.anycollect.core.api.internal.Cancellation;

public class NoopCancelation implements Cancellation {
    @Override
    public void cancel() {

    }
}
