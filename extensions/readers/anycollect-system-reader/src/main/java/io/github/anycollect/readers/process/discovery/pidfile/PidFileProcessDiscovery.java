package io.github.anycollect.readers.process.discovery.pidfile;

import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.annotations.InstanceId;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.process.Process;
import io.github.anycollect.readers.process.discovery.ProcessDiscovery;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Extension(name = PidFileProcessDiscovery.NAME, point = ProcessDiscovery.class)
public final class PidFileProcessDiscovery extends ProcessDiscovery {
    public static final String NAME = "PidFileProcessDiscovery";
    private final WatchService watchService;
    private final Map<Path, Process> processes;
    private final OperatingSystem os;

    @ExtCreator
    public PidFileProcessDiscovery(@ExtConfig @Nonnull final PidFileProcessDiscoveryConfig config,
                                   @InstanceId @Nonnull final String id) throws ConfigurationException {
        super(id);
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new ConfigurationException("could not create watch service to monitor changes in pid files", e);
        }
        os = new SystemInfo().getOperatingSystem();
        processes = new HashMap<>();
        for (String pidFilePath : config.pidFiles()) {
            Path pidFile = Paths.get(pidFilePath);
            try {
                pidFile.register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY);
            } catch (IOException e) {
                throw new ConfigurationException("could not watch pid file: " + pidFile, e);
            }
            processes.put(pidFile, createProcess(pidFile));
        }
    }

    @Override
    public Set<Process> discover() {
        WatchKey watchKey = watchService.poll();
        for (WatchEvent<?> event : watchKey.pollEvents()) {
            Path pidFile = (Path) event.context();
            processes.put(pidFile, createProcess(pidFile));
        }
        Set<Process> result = new HashSet<>();
        for (Map.Entry<Path, Process> entry : processes.entrySet()) {
            Process process = entry.getValue();
            if (process == null) {
                process = createProcess(entry.getKey());
                if (process != null) {
                    result.add(process);
                }
            } else {
                result.add(process);
            }
        }
        return result;
    }

    @Nullable
    private Process createProcess(final Path pidFile) {
        int pid;
        try {
            pid = Integer.parseInt(Files.readAllLines(pidFile, StandardCharsets.UTF_8).get(0));
        } catch (IOException e) {
            return null;
        }
        OSProcess process = os.getProcess(pid);
        Tags meta = createMeta(process);
        return new Process(pid, Tags.of("pid.file", pidFile.toFile().getAbsolutePath()), meta);
    }
}
