package io.github.anycollect.core.impl.writers.socket;

import javax.annotation.Nonnull;
import java.io.IOException;

public interface Sender {
    void connected() throws IOException;

    boolean isConnected();

    void send(@Nonnull String data) throws IOException;

    void flush() throws IOException;

    void closed();
}
