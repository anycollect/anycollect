package io.github.anycollect.readers.process;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.PullManager;
import io.github.anycollect.core.api.internal.QueryMatcher;
import io.github.anycollect.core.api.internal.QueryMatcherResolver;
import io.github.anycollect.core.api.query.QueryProvider;
import io.github.anycollect.extensions.annotations.*;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OperatingSystem;

import javax.annotation.Nonnull;

@Extension(name = ProcessReader.NAME, point = Reader.class)
public final class ProcessReader implements Reader {
    public static final String NAME = "ProcessReader";
    private final PullManager pullManager;
    private final ProcessDiscovery discovery;
    private final Config config;
    private final String id;

    @ExtCreator
    public ProcessReader(@ExtDependency(qualifier = "puller") @Nonnull final PullManager pullManager,
                         @ExtDependency(qualifier = "discovery") @Nonnull final ProcessDiscovery discovery,
                         @ExtConfig @Nonnull final Config config,
                         @InstanceId @Nonnull final String id) {
        this.pullManager = pullManager;
        this.discovery = discovery;
        this.config = config;
        this.id = id;
    }

    @Override
    public void start(@Nonnull final Dispatcher dispatcher) {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem os = systemInfo.getOperatingSystem();
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        pullManager.start(discovery, QueryProvider.singleton(new ProcessStats(os, memory)),
                QueryMatcherResolver.consistent(QueryMatcher.all(config.period)), dispatcher);
    }

    @Override
    public String getId() {
        return id;
    }

    public static final class Config {
        private final int period;

        @JsonCreator
        public Config(@JsonProperty(value = "period", required = true) final int period) {
            this.period = period;
        }
    }
}
