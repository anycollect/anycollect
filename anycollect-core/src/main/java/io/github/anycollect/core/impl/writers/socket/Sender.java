package io.github.anycollect.core.impl.writers.socket;

import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;
import java.io.IOException;

public interface Sender {
    void connected() throws IOException;

    boolean isConnected();

    void send(@Nonnull Sample sample) throws SerialisationException, IOException;

    void flush() throws IOException;

    void closed();
}
