package io.github.anycollect.core.impl.serializers.graphite;

import com.google.common.collect.Lists;
import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.metric.Key;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Sample;
import io.github.anycollect.metric.Tags;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GraphiteSerializerTest {
    @Test
    void reuseTest() throws Exception {
        GraphiteSerializer serializer = new GraphiteSerializer(GraphiteSerializerConfig.DEFAULT);
        Sample sample = Metric.builder()
                .key("anycollect/test/test.test")
                .tags(Tags.of("test/test.test", "value", "test.test2", "value2"))
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

    @Test
    void hierarchicalTest() throws SerialisationException {
        Sample sample = Metric.builder()
                .key("anycollect/jvm/runtime/uptime")
                .tags(Tags.of("host", "localhost"))
                .gauge()
                .sample(1, 0);
        GraphiteSerializerConfig cfg = GraphiteSerializerConfig.builder()
                .prefix(Key.of("prefix"))
                .tags(Tags.of("env", "test"))
                .tagSupport(false)
                .tagsAsPrefix(Lists.newArrayList(Key.of("env"), Key.of("host")))
                .build();
        GraphiteSerializer serializer = new GraphiteSerializer(cfg);
        assertThat(serializer.serialize(sample))
                .isEqualTo("prefix.env.test.host.localhost.anycollect.jvm.runtime.uptime.gauge 1.0 0\n");
    }
}