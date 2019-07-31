package io.github.anycollect.core.impl.transform;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.extensions.Instance;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Sample;
import io.github.anycollect.test.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TransformerTest {
    private Transformer transformer;

    @BeforeEach
    void createPullManager() throws Exception {
        TestContext context = new TestContext("transformer.yaml");
        Instance instance = context.getInstance(Transformer.class);
        transformer = (Transformer) instance.resolve();
    }

    @Test
    @DisplayName("is successfully instantiated by extension system")
    void isInstantiatedBySystem() {
        assertThat(transformer).isNotNull();
    }

    @Nested
    class WhenStartTransformer {
        private Dispatcher dispatcher;

        @BeforeEach
        void setUp() {
            dispatcher = mock(Dispatcher.class);
            transformer.start(dispatcher);
        }

        @Test
        void processRenamed() {
            long timestamp = System.currentTimeMillis();
            transformer.submit(Collections.singletonList(
                    Metric.builder()
                            .key("test")
                            .tag("pid.file", "/home/test/anycollect.pid")
                            .gauge()
                            .sample(1.0, timestamp)
            ));
            ArgumentCaptor<Sample> captor = ArgumentCaptor.forClass(Sample.class);
            verify(dispatcher, times(1)).dispatch(captor.capture());
            assertThat(captor.getValue().getMetric())
                    .isEqualTo(Metric.builder()
                            .key("test")
                            .tag("process", "anycollect")
                            .gauge());
        }
    }
}