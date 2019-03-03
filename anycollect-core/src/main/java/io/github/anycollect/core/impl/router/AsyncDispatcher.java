package io.github.anycollect.core.impl.router;

import io.github.anycollect.core.api.dispatcher.Dispatcher;

public interface AsyncDispatcher extends Dispatcher {
    void stop();
}
