package io.github.anycollect.readers.process.discovery.pidfile;

import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.process.Process;
import io.github.anycollect.readers.process.discovery.ProcessDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(PidFileProcessDiscovery.class);
    private final WatchService watchService;
    private final Set<Path> pidFiles;
    private final Map<Path, Process> processes;
    private final OperatingSystem os;

    @ExtCreator
    public PidFileProcessDiscovery(@ExtConfig @Nonnull final PidFileProcessDiscoveryConfig config)
            throws ConfigurationException {
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new ConfigurationException("could not create watch service to monitor changes in pid files", e);
        }
        os = new SystemInfo().getOperatingSystem();
        processes = new HashMap<>();
        pidFiles = new HashSet<>();
        for (String pidFilePath : config.pidFiles()) {
            Path pidFile = Paths.get(pidFilePath);
            if (pidFile.toFile().isDirectory()) {
                throw new ConfigurationException("pid file must not be directory");
            }
            pidFiles.add(pidFile);
            Path pidDir = pidFile.getParent();
            try {
                pidDir.register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY);
            } catch (IOException e) {
                throw new ConfigurationException("could not watch pid file: " + pidFile, e);
            }
            Process process = createProcess(pidFile);
            if (process != null) {
                processes.put(pidFile, process);
            }
        }
    }

    @Override
    public Set<Process> discover() {
        WatchKey watchKey = watchService.poll();
        if (watchKey != null) {
            for (WatchEvent<?> event : watchKey.pollEvents()) {
                Path pidFile = ((Path) watchKey.watchable()).resolve((Path) event.context());
                Path found = null;
                for (Path path : pidFiles) {
                    if (path.equals(pidFile)) {
                        found = path;
                        break;
                    }
                }
                if (found != null && (event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)
                        || event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)
                        || event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE))) {
                    LOG.debug("pid file {} has been changed", pidFile);
                    processes.remove(found);
                }
            }
            watchKey.reset();
        }
        Set<Process> result = new HashSet<>();
        for (Path path : pidFiles) {
            if (!processes.containsKey(path)) {
                Process process = createProcess(path);
                if (process != null) {
                    result.add(process);
                    processes.put(path, process);
                }
            } else {
                result.add(processes.get(path));
            }
        }
        return result;
    }

    @Nullable
    private Process createProcess(final Path pidFile) {
        if (!pidFile.toFile().exists()) {
            LOG.debug("pid file {} has not been created yet", pidFile);
            return null;
        }
        int pid;
        try {
            pid = Integer.parseInt(Files.readAllLines(pidFile, StandardCharsets.UTF_8).get(0));
        } catch (IOException e) {
            LOG.warn("could not read pid from pid file {}", pidFile, e);
            return null;
        }
        OSProcess process = os.getProcess(pid);
        if (process == null) {
            LOG.debug("there is no process with pid {} for now", pid);
            return null;
        }
        Tags meta = createMeta(process);
        return new Process("pid@" + pid, pid, Tags.of("pid.file", pidFile.toFile().getAbsolutePath()), meta);
    }
}
