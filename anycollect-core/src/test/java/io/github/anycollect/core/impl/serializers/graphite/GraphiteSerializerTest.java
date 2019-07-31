package io.github.anycollect.core.impl.serializers.graphite;

import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Sample;
import io.github.anycollect.metric.Tags;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GraphiteSerializerTest {
    private GraphiteSerializer serializer = new GraphiteSerializer(GraphiteSerializerConfig.DEFAULT);

    @Test
    void reuseTest() throws Exception {
        Sample sample = Metric.builder()
                .key("anycollect/test/test.test")
                .tags(Tags.of("test/test.test", "value", "test.test2", "value2"))
                .empty()
                .gauge()
                .sample(1, 0);
        String string = serializer.serialize(sample);
        assertThat(string).isEqualTo(
                "anycollect.test.testTest.gauge;testTestTest=value;testTest2=value2 1.0 0\n"
        );
        String repeat = serializer.serialize(sample);
        assertThat(repeat).isEqualTo(
                "anycollect.test.testTest.gauge;testTestTest=value;testTest2=value2 1.0 0\n"
        );
    }
}