package io.github.anycollect;

import ch.qos.logback.classic.util.ContextInitializer;
import io.github.anycollect.extensions.substitution.VarSubstitutor;
import io.github.anycollect.shutdown.RemoveFileShutdownTask;
import io.github.anycollect.shutdown.ShutdownHook;
import io.github.anycollect.shutdown.ShutdownTask;
import oshi.SystemInfo;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        AnyCollect anyCollect;
        try {
            if (config.getPidFile() != null) {
                int pid = new SystemInfo().getOperatingSystem().getProcessId();
                Path path = config.getPidFile().toPath();
                Files.write(path, Collections.singletonList(Integer.toString(pid)),
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                shutdownTasks.add(new RemoveFileShutdownTask(path));
            }
            VarSubstitutor substitutor = VarSubstitutor.firstNonNull(
                    VarSubstitutor.ofMap(config.getEnv()),
                    VarSubstitutor.env(),
                    VarSubstitutor.ofClassPathFile("preconfigured/default-vars.properties")
            );
            final List<String> extensionClassPathFiles;
            if (!config.getEnabledPreconfiguredExtensions().isEmpty()) {
                List<String> extensionNames = config.getEnabledPreconfiguredExtensions();
                extensionNames.add(0, "core");
                extensionNames.add("router");
                extensionClassPathFiles = extensionNames.stream()
                        .map(extensionName -> "preconfigured" + File.separator + extensionName + ".yaml")
                        .collect(Collectors.toList());
            } else {
                extensionClassPathFiles = Collections.emptyList();
            }
            anyCollect = new AnyCollect(config.getConfigFile(), extensionClassPathFiles, substitutor);
            shutdownTasks.add(anyCollect::shutdown);
        } finally {
            Runtime.getRuntime().addShutdownHook(new ShutdownHook(shutdownTasks));
        }
        anyCollect.run();
    }
}
