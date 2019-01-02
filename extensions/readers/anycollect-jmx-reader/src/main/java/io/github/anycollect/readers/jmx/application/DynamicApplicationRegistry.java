package io.github.anycollect.readers.jmx.application;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface DynamicApplicationRegistry {
    ApplicationRegistry getCurrentSnapshot();
}
