package io.github.anycollect.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.anycollect.metric.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// TODO add exceptional tests: missed required property or incorrect value
class AnyCollectModuleTest {
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new AnyCollectModule());
    }

    @Test
    void requiredTagsAndMeta() throws Exception {
        MetricId id = MetricId.builder()
                .key("test")
                .unit("tests")
                .stat(Stat.MAX)
                .type(Type.GAUGE)
                .meta("daemon", "anycollect")
                .build();
        Metric metric = Metric.of(id, 12.0, System.currentTimeMillis());
        String json = mapper.writeValueAsString(metric);
        Metric restored = mapper.readValue(json, Metric.class);
        assertThat(metric).isEqualToComparingFieldByFieldRecursively(restored);
    }

    @Test
    void metaIsOptional() throws Exception {
        String json = "{\"id\":{\"tags\":{\"what\":\"test\",\"unit\":\"tests\",\"stat\":\"max\",\"mtype\":\"gauge\"}},\"value\":15.0,\"timestamp\":1550273897001}";
        Metric restored = mapper.readValue(json, Metric.class);
        assertThat(restored.getId().getMetaTags()).isEqualTo(Tags.empty());
    }
}