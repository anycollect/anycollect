package io.github.anycollect.core.api.common;

public interface Lifecycle {
    /**
     * Initializes the service or do nothing if it has been already initialized.
     * <p>
     * May took time
     */
    default void init() {
    }

    /**
     * Destroys the service or do nothing if it has been already destroyed.
     * <p>
     * May took time
     */
    default void destroy() {
    }
}
