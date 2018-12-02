package io.github.anycollect.extensions.di;

public interface Injector {
    <T> T getInstance(Class<T> type);

    Object getInstance(String name);
}
