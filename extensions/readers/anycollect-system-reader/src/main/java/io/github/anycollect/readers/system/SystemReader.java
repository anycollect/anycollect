package io.github.anycollect.readers.system;

import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Cancellation;
import io.github.anycollect.core.api.internal.PullManager;
import io.github.anycollect.extensions.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.software.os.FileSystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Extension(name = SystemReader.NAME, contracts = Reader.class)
public final class SystemReader implements Reader, Lifecycle {
    public static final String NAME = "SystemReader";
    private static final Logger LOG = LoggerFactory.getLogger(SystemReader.class);
    private final PullManager puller;
    private final SystemConfig config;
    private final String id;
    private volatile Cancellation cancellation;

    @ExtCreator
    public SystemReader(@ExtDependency(qualifier = "puller") @Nonnull final PullManager puller,
                        @ExtConfig(optional = true) @Nullable final SystemConfig config,
                        @InstanceId @Nonnull final String id) {
        this.puller = puller;
        this.config = config != null ? config : SystemConfig.DEFAULT;
        this.id = id;
    }

    @Override
    public void start(@Nonnull final Dispatcher dispatcher) {
        SystemInfo systemInfo = new SystemInfo();
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        CpuUsage cpuUsage = new CpuUsage(processor, config.cpu());
        FileSystem fileSystem = systemInfo.getOperatingSystem().getFileSystem();
        FileSystemUsage fsUsage = new FileSystemUsage(fileSystem, config.fs());
        this.cancellation = puller.start(id, cpuUsage, dispatcher, config.cpu().period())
                .andThen(
                        puller.start(id, fsUsage, dispatcher, config.fs().period()));
    }

    @Override
    public void stop() {
        if (cancellation != null) {
            cancellation.cancel();
        }
        LOG.info("{}({}) has been successfully stopped", id, NAME);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void init() {
        LOG.info("{}({}) has been successfully initialized", id, NAME);
    }

    @Override
    public void destroy() {
        LOG.info("{}({}) has been successfully destroyed", id, NAME);
    }
}
