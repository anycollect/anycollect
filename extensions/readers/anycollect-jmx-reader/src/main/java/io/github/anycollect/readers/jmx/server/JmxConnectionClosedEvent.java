package io.github.anycollect.readers.jmx.server;

public final class JmxConnectionClosedEvent implements JmxEvent {
    @Override
    public JmxEventType getType() {
        return JmxEventType.CONNECTION_CLOSED;
    }
}
