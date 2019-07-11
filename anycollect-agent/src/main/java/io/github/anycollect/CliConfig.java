package io.github.anycollect;

import lombok.Getter;
import picocli.CommandLine;

import java.io.File;
import java.util.Map;

@Getter
public final class CliConfig {
    @CommandLine.Option(names = {"-c", "--conf"},
            required = true,
            description = "This is the path to configuration")
    private File configFile;

    @CommandLine.Option(names = {"-p", "--pid-file"},
            description = "This is the path to store a PID file "
                    + "which will contain the process ID of the anycollect process.")
    private File pidFile;

    @CommandLine.Option(names = {"-l", "--logback-conf"},
            description = "This is the path to load logback configuration from")
    private File logbackConfig;

    @CommandLine.Option(names = {"-e", "--env"},
            description = "This is the map of environment variables (can be referenced using \"!var\" tag in yaml")
    private Map<String, String> env;
}
