package io.github.anycollect.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.anycollect.assertj.AnyCollectAssertions;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;
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
    void metricDeserializeTest() throws IOException  {
        Metric metric = mapper.readValue("{\"key\":\"test\",\"tags\":{\"host\":\"localhost\",\"tag1\":\"val1\"},\"meta\":{\"meta1\":\"val2\"},\"measurements\":[{\"stat\":\"value\",\"mtype\":\"gauge\",\"unit\":\"ms\",\"value\":1}],\"timestamp\":15630695420000}", Metric.class);
        AnyCollectAssertions.assertThat(metric)
                .hasKey("test")
                .hasTags("host", "localhost", "tag1", "val1")
                .hasMeta("meta1", "val2")
                .hasMeasurement(Stat.VALUE, Type.GAUGE, "ms", 1);
    }
}