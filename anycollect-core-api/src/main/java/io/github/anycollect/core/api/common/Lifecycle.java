package io.github.anycollect.core.api.common;

public interface Lifecycle {
    default void init() {
    }

    default void destroy() {
    }
}
