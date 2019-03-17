package io.github.anycollect.readers.system;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.query.SelfQuery;
import io.github.anycollect.core.api.target.SelfTarget;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;
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
    public List<Metric> executeOn(@Nonnull final SelfTarget target) {
        List<Metric> metrics = new ArrayList<>();
        if (config.reportOpenDescriptors()) {
            long openFileDescriptors = fileSystem.getOpenFileDescriptors();
            metrics.add(Metric.builder()
                    .key("fs.open.descriptors")
                    .concatTags(target.getTags())
                    .concatMeta(target.getMeta())
                    .at(clock.wallTime())
                    .measurement(Stat.VALUE, Type.GAUGE, "descriptors", openFileDescriptors)
                    .build()
            );
        }
        long timestamp = clock.wallTime();
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
            metrics.add(Metric.builder()
                    .key("fs.usage")
                    .concatTags(target.getTags())
                    .tag("mount", mount)
                    .tag("fs", fileSystem)
                    .tag("device", device)
                    .measurement(Stat.VALUE, Type.GAUGE, "percents", usage)
                    .concatMeta(target.getMeta())
                    .at(timestamp)
                    .build());
        }
        return metrics;
    }
}
