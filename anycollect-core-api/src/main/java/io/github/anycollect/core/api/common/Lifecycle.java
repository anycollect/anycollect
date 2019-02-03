package io.github.anycollect.core.api.common;

public interface Lifecycle {
    /**
     * Initializes the service or do nothing if it has been already initialized.
     *
     * May took time
     */
    default void init() {
    }

    /**
     * Destroys the service or do nothing if it has been already destroyed.
     *
     * May took time
     */
    default void destroy() {
    }
}
