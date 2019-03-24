package io.github.anycollect;

import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.PullManager;
import io.github.anycollect.core.api.internal.QueryMatcher;
import io.github.anycollect.core.api.internal.QueryMatcherResolver;
import io.github.anycollect.core.impl.readers.registry.MeterRegistryReader;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.readers.jmx.JmxReader;
import io.github.anycollect.readers.jmx.discovery.CurrentApp;
import io.github.anycollect.readers.jmx.query.JvmMetrics;
import io.github.anycollect.readers.process.ProcessReader;
import io.github.anycollect.readers.process.ProcessReaderConfig;
import io.github.anycollect.readers.process.discovery.current.CurrentProcessDiscovery;
import io.github.anycollect.readers.process.discovery.current.CurrentProcessDiscoveryConfig;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Extension(name = InternalReader.NAME, point = Reader.class)
public final class InternalReader implements Reader {
    public static final String NAME = "InternalReader";
    private final List<Reader> internalReaders;

    @ExtCreator
    public InternalReader(@ExtDependency(qualifier = "puller") @Nonnull final PullManager pullManager,
                          @ExtDependency(qualifier = "registry") @Nonnull final MeterRegistry registry,
                          @ExtConfig @Nonnull final InternalMonitoringConfig config) {
        internalReaders = new ArrayList<>();
        addJvmMonitoring(pullManager, registry, config);
        addProcessMonitoring(pullManager, config);
        addLogicMonitoring(pullManager, registry, config);
    }

    @Override
    public void start(@Nonnull final Dispatcher dispatcher) {
        for (Reader internalReader : internalReaders) {
            internalReader.start(dispatcher);
        }
    }

    @Override
    public String getId() {
        return "internal";
    }

    private void addJvmMonitoring(@Nonnull final PullManager pullManager,
                                  @Nonnull final MeterRegistry registry,
                                  @Nonnull final InternalMonitoringConfig config) {
        JvmConfig jvm = config.jvm();
        if (jvm.enabled()) {
            int period = config.period();
            if (jvm.period() > 0) {
                period = jvm.period();
            }
            CurrentApp currentJvmDiscovery = new CurrentApp(registry, new CurrentApp.Config(jvm.applicationName()));
            JmxReader jmxReader = new JmxReader(
                    pullManager,
                    Collections.singletonList(currentJvmDiscovery),
                    Collections.singletonList(new JvmMetrics()),
                    QueryMatcherResolver.consistent(QueryMatcher.all(period)),
                    "internal.jmx");
            internalReaders.add(jmxReader);
        }
    }

    private void addProcessMonitoring(@Nonnull final PullManager pullManager,
                                      @Nonnull final InternalMonitoringConfig config) {
        int period = config.period();
        ProcessConfig process = config.process();
        if (!process.collectCpuUsage() && !process.collectMemoryUsage()) {
            return;
        }
        if (process.period() > 0) {
            period = process.period();
        }
        ProcessReader processReader = new ProcessReader(
                pullManager,
                new CurrentProcessDiscovery(CurrentProcessDiscoveryConfig.builder()
                        .tags(process.tags())
                        .build(),
                        ""),
                ProcessReaderConfig.builder()
                        .collectCpuUsage(process.collectCpuUsage())
                        .collectMemoryUsage(process.collectMemoryUsage())
                        .period(period)
                        .build(),
                "internal.process");
        internalReaders.add(processReader);
    }

    private void addLogicMonitoring(@Nonnull final PullManager pullManager,
                                    @Nonnull final MeterRegistry registry,
                                    @Nonnull final InternalMonitoringConfig config) {
        MeterRegistryConfig meterRegistry = config.registry();
        if (meterRegistry.enabled()) {
            MeterRegistryReader meterRegistryReader = new MeterRegistryReader(
                    pullManager,
                    registry,
                    "internal.registry");
            internalReaders.add(meterRegistryReader);
        }
    }
}
