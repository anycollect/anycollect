package io.github.anycollect;

import ch.qos.logback.classic.util.ContextInitializer;
import io.github.anycollect.extensions.VarSubstitutor;
import io.github.anycollect.shutdown.RemoveFileShutdownTask;
import io.github.anycollect.shutdown.ShutdownHook;
import io.github.anycollect.shutdown.ShutdownTask;
import oshi.SystemInfo;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Init {
    private Init() {
    }

    public static void main(final String... args) throws Exception {
        CliConfig config = new CliConfig();
        new CommandLine(config).parse(args);
        if (config.getLogbackConfig() != null) {
            System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, config.getLogbackConfig().getAbsolutePath());
        }
        List<ShutdownTask> shutdownTasks = new ArrayList<>();
        if (config.getPidFile() != null) {
            int pid = new SystemInfo().getOperatingSystem().getProcessId();
            Path path = config.getPidFile().toPath();
            Files.write(path, Collections.singletonList(Integer.toString(pid)),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            shutdownTasks.add(new RemoveFileShutdownTask(path));
        }
        VarSubstitutor substitutor;
        if (config.getEnv() != null) {
            substitutor = VarSubstitutor.firstNonNull(
                    VarSubstitutor.ofMap(config.getEnv()),
                    VarSubstitutor.env()
            );
        } else {
            substitutor = VarSubstitutor.env();
        }
        AnyCollect anyCollect = new AnyCollect(config.getConfigFile(), substitutor);
        shutdownTasks.add(anyCollect::shutdown);
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(shutdownTasks));
        anyCollect.run();
    }
}
