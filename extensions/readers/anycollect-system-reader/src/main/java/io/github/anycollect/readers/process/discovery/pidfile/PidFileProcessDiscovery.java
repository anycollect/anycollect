package io.github.anycollect.readers.process.discovery.pidfile;

import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.process.EphemeralProcess;
import io.github.anycollect.readers.process.LiveProcess;
import io.github.anycollect.readers.process.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Extension(name = PidFileProcessDiscovery.NAME, point = ServiceDiscovery.class)
public final class PidFileProcessDiscovery implements ServiceDiscovery<Process> {
    public static final String NAME = "PidFileProcessDiscovery";
    private static final Logger LOG = LoggerFactory.getLogger(PidFileProcessDiscovery.class);
    private final WatchService watchService;
    private final Set<PidFileTargetDefinition> targets;
    private final Map<PidFileTargetDefinition, Process> processes;
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
        targets = new HashSet<>();
        for (PidFileTargetDefinition target : config.watch()) {
            Path pidFile = target.file();
            if (pidFile.toFile().isDirectory()) {
                throw new ConfigurationException("pid file must not be directory");
            }
            targets.add(target);
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
            Process process = createProcess(target);
            processes.put(target, process);
        }
    }

    @Override
    public Set<Process> discover() {
        // remove processes whose pid files have been changed to refresh them
        WatchKey watchKey = watchService.poll();
        if (watchKey != null) {
            for (WatchEvent<?> event : watchKey.pollEvents()) {
                Path pidFile = ((Path) watchKey.watchable()).resolve((Path) event.context());
                PidFileTargetDefinition found = null;
                for (PidFileTargetDefinition def : targets) {
                    if (def.file().equals(pidFile)) {
                        found = def;
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
        // delete ephemeral processes to refresh them
        for (PidFileTargetDefinition target : targets) {
            Process process = processes.get(target);
            if (process instanceof EphemeralProcess) {
                processes.remove(target);
            }
        }
        // form result target set
        Set<Process> result = new HashSet<>();
        for (PidFileTargetDefinition target : targets) {
            if (!processes.containsKey(target)) {
                Process process = createProcess(target);
                result.add(process);
                processes.put(target, process);
            } else {
                result.add(processes.get(target));
            }
        }
        return result;
    }

    @Nonnull
    private Process createProcess(final PidFileTargetDefinition def) {
        Path pidFile = def.file();
        if (!pidFile.toFile().exists()) {
            LOG.debug("pid file {} has not been created yet", pidFile);
            return new EphemeralProcess(def.targetId(), def.tags(), Tags.empty());
        }
        int pid;
        try {
            pid = Integer.parseInt(Files.readAllLines(pidFile, StandardCharsets.UTF_8).get(0));
        } catch (IOException | NumberFormatException e) {
            LOG.warn("could not read pid from pid file {}", pidFile, e);
            return new EphemeralProcess(def.targetId(), def.tags(), Tags.empty());
        }
        OSProcess process = os.getProcess(pid);
        if (process == null) {
            LOG.debug("there is no process with pid {} for now", pid);
            return new EphemeralProcess(def.targetId(), def.tags(), Tags.empty());
        }
        return new LiveProcess(os, def.targetId(), pid, def.tags(), Tags.of("pid.file", pidFile.toFile().getAbsolutePath()));
    }
}
