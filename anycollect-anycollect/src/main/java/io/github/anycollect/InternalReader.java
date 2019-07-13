package io.github.anycollect;

import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.common.Lifecycle;
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
import io.github.anycollect.readers.jmx.query.JvmMetricsConfig;
import io.github.anycollect.readers.process.ProcessReader;
import io.github.anycollect.readers.process.ProcessReaderConfig;
import io.github.anycollect.readers.process.discovery.current.CurrentProcessDiscovery;
import io.github.anycollect.readers.process.discovery.current.CurrentProcessDiscoveryConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Extension(name = InternalReader.NAME, point = Reader.class)
public final class InternalReader implements Reader, Lifecycle {
    public static final String NAME = "InternalReader";
    private final List<Reader> internalReaders;

    @ExtCreator
    public InternalReader(@ExtDependency(qualifier = "puller") @Nonnull final PullManager pullManager,
                          @ExtDependency(qualifier = "registry") @Nonnull final MeterRegistry registry,
                          @ExtConfig(optional = true) @Nullable final InternalMonitoringConfig optConfig) {
        internalReaders = new ArrayList<>();
        InternalMonitoringConfig config = optConfig != null ? optConfig : InternalMonitoringConfig.DEFAULT;
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
    public void stop() {
        for (Reader reader : internalReaders) {
            reader.stop();
        }
    }

    @Override
    public String getId() {
        return "internal";
    }

    @Override
    public void init() {
        for (Reader reader : internalReaders) {
            if (reader instanceof Lifecycle) {
                ((Lifecycle) reader).init();
            }
        }
    }

    @Override
    public void destroy() {
        for (Reader reader : internalReaders) {
            if (reader instanceof Lifecycle) {
                ((Lifecycle) reader).destroy();
            }
        }
    }

    private void addJvmMonitoring(@Nonnull final PullManager pullManager,
                                  @Nonnull final MeterRegistry registry,
                                  @Nonnull final InternalMonitoringConfig config) {
        MetricConfig jvm = config.jvm();
        if (jvm.enabled()) {
            int period = config.period();
            if (jvm.period() > 0) {
                period = jvm.period();
            }
            CurrentApp currentJvmDiscovery = new CurrentApp(registry,
                    new CurrentApp.Config("anycollect-jvm", config.tags(), config.meta()));
            JmxReader jmxReader = new JmxReader(
                    pullManager,
                    Collections.singletonList(currentJvmDiscovery),
                    Collections.singletonList(new JvmMetrics(
                            JvmMetricsConfig.builder()
                            .prefix(config.prefix())
                            .tags(config.tags())
                            .meta(config.meta())
                            .build()
                    )),
                    QueryMatcherResolver.consistent(QueryMatcher.all(period)),
                    "internal.jmx");
            internalReaders.add(jmxReader);
        }
    }

    private void addProcessMonitoring(@Nonnull final PullManager pullManager,
                                      @Nonnull final InternalMonitoringConfig config) {
        int period = config.period();
        if (!config.cpu().enabled() && !config.mem().enabled()) {
            return;
        }
        if (config.cpu().period() > 0) {
            period = config.cpu().period();
        }
        if (config.mem().period() > 0 && config.cpu().period() > config.mem().period()) {
            period = config.mem().period();
        }
        ProcessReader processReader = new ProcessReader(
                pullManager,
                new CurrentProcessDiscovery(
                        CurrentProcessDiscoveryConfig.builder()
                                .targetId("anycollect-process")
                                .tags(config.tags())
                                .meta(config.meta())
                                .build()),
                ProcessReaderConfig.builder()
                        .prefix(config.prefix())
                        .collectCpuUsage(config.cpu().enabled())
                        .collectMemoryUsage(config.mem().enabled())
                        .period(period)
                        .build(),
                "internal.process");
        internalReaders.add(processReader);
    }

    private void addLogicMonitoring(@Nonnull final PullManager pullManager,
                                    @Nonnull final MeterRegistry registry,
                                    @Nonnull final InternalMonitoringConfig config) {
        if (config.logic().enabled()) {
            MeterRegistryReader meterRegistryReader = new MeterRegistryReader(
                    pullManager,
                    registry,
                    "internal.registry");
            internalReaders.add(meterRegistryReader);
        }
    }
}
