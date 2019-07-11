package io.github.anycollect;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class CliConfigTest {
    @Test
    void parseTest() {
        CliConfig config = new CliConfig();
        new CommandLine(config).parse("--conf", "test.yaml", "-e", "HOST=localhost");
        assertThat(config.getEnv()).containsEntry("HOST", "localhost");
        assertThat(config.getConfigFile()).isEqualTo(new File("test.yaml"));
    }
}