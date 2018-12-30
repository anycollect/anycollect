package io.github.anycollect.readers.jmx.monitoring;

public final class MonitoringConstants {
    public static final String DOMAIN = "anycollect";
    public static final String CONNECTION_POOL_IDLE = "connections_pool_idle";
    public static final String CONNECTION_POOL_ACTIVE = "connections_pool_active";
    public static final String CONNECTION_POOL_INVALIDATED = "connections_pool_invalidated";
    public static final String APPLICATION_TAG = "application";
    public static final String SERVER_TAG = "server";

    private MonitoringConstants() {
        throw new UnsupportedOperationException("this is a utility class, do not instantiate it");
    }
}
