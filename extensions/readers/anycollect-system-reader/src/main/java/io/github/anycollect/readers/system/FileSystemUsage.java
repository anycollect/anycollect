package io.github.anycollect.readers.system;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.query.SelfQuery;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Sample;
import io.github.anycollect.metric.Tags;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class FileSystemUsage extends SelfQuery {
    private final FileSystem fileSystem;
    private final FileSystemConfig config;
    private final Clock clock;

    public FileSystemUsage(@Nonnull final FileSystem fileSystem,
                           @Nonnull final FileSystemConfig config) {
        super("fs.usage");
        this.fileSystem = fileSystem;
        this.config = config;
        this.clock = Clock.getDefault();
    }

    @Override
    public List<Sample> execute() {
        List<Sample> samples = new ArrayList<>();
        long timestamp = clock.wallTime();
        if (config.reportOpenDescriptors()) {
            long openFileDescriptors = fileSystem.getOpenFileDescriptors();
            samples.add(Metric.builder()
                    .key("fs/open.descriptors")
                    .gauge()
                    .sample(openFileDescriptors, timestamp)
            );
        }
        for (OSFileStore fileStore : fileSystem.getFileStores()) {
            long usableSpace = fileStore.getUsableSpace();
            long totalSpace = fileStore.getTotalSpace();
            String fileSystem = fileStore.getType();
            if (config.ignoreFileSystems().contains(fileSystem)) {
                continue;
            }
            double usage = 100.0 * (totalSpace - usableSpace) / totalSpace;
            String mount = fileStore.getMount();
            String device = fileStore.getVolume();
            samples.add(Metric.builder()
                    .key("fs/usage")
                    .tags(Tags.of("mount", mount, "fs", fileSystem, "device", device))
                    .gauge("percents")
                    .sample(usage, timestamp)
            );
        }
        return samples;
    }
}
