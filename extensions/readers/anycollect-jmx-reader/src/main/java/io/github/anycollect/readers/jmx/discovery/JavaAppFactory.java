package io.github.anycollect.readers.jmx.discovery;

import io.github.anycollect.core.api.target.TargetFactory;
import io.github.anycollect.readers.jmx.config.JavaAppConfig;
import io.github.anycollect.readers.jmx.server.JavaApp;

public interface JavaAppFactory extends TargetFactory<JavaAppConfig, JavaApp> {
}
