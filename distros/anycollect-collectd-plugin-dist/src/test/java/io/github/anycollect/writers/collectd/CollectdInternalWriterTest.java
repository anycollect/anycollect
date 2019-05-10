package io.github.anycollect.writers.collectd;

import io.github.anycollect.extensions.Instance;
import io.github.anycollect.test.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CollectdInternalWriterTest {
    private CollectdInternalWriter collectd;

    @BeforeEach
    void createCollectdWriter() throws Exception {
        TestContext context = new TestContext("anycollect.yaml");
        Instance instance = context.getInstance("collectd");
        this.collectd = (CollectdInternalWriter) instance.resolve();
    }

    @Test
    @DisplayName("is successfully instantiated by extension system")
    void isInstantiatedBySystem() {
        assertThat(collectd).isNotNull();
    }
}