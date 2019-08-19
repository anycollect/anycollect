package io.github.anycollect.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.anycollect.assertj.AnyCollectAssertions;
import io.github.anycollect.metric.Sample;
import io.github.anycollect.metric.Stat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

// TODO add exceptional tests: missed required property or incorrect value
class AnyCollectModuleTest {
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new AnyCollectModule());
    }

    @Test
    void metricDeserializeTest() throws IOException {
        String json = "" +
                "{\n" +
                "  \"key\": \"anycollect/pull.manager/processing.time\",\n" +
                "  \"tags\": {\n" +
                "    \"host\": \"localhost\"\n" +
                "  },\n" +
                "  \"meta\": {\n" +
                "    \"test\": \"true\"\n" +
                "  },\n" +
                "  \"stat\": \"mean\",\n" +
                "  \"mtype\": \"aggregate\",\n" +
                "  \"unit\": \"ms\",\n" +
                "  \"timestamp\": 123,\n" +
                "  \"value\": 1.3\n" +
                "}";
        Sample sample = mapper.readValue(json, Sample.class);
        System.out.println(sample);
        AnyCollectAssertions.assertThat(sample)
                .hasKey("anycollect/pull.manager/processing.time")
                .hasTags("host", "localhost")
                .hasMeta("test", "true")
                .hasMetric(Stat.MEAN, "ms", 1.3);
    }
}