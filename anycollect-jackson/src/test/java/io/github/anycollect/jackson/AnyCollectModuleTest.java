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
        PointId id = PointId.key("test")
                .unit("tests")
                .stat(Stat.MAX)
                .type(Type.GAUGE)
                .meta("daemon", "anycollect")
                .build();
        Point point = Point.of(id, 12.0, System.currentTimeMillis());
        String json = mapper.writeValueAsString(point);
        Point restored = mapper.readValue(json, Point.class);
        assertThat(point).isEqualToComparingFieldByFieldRecursively(restored);
    }

    @Test
    void tagsAndMetaAreOptional() throws Exception {
        String json = "{\"id\":{\"what\":\"test\",\"unit\":\"tests\",\"stat\":\"max\",\"mtype\":\"gauge\",\"tags\":{}},\"value\":15.0,\"timestamp\":1550273897001}";
        Point restored = mapper.readValue(json, Point.class);
        assertThat(restored.getId().getMetaTags()).isEqualTo(Tags.empty());
        assertThat(restored.getId().getKey()).isEqualTo("test");
        assertThat(restored.getId().getUnit()).isEqualTo("tests");
        assertThat(restored.getId().getStat()).isEqualTo(Stat.max());
        assertThat(restored.getId().getType()).isEqualTo(Type.GAUGE);
    }

    @Test
    void statSerialization() throws Exception {
        PointId id = PointId.key("test")
                .unit("tests")
                .stat(Stat.MAX)
                .type(Type.GAUGE)
                .build();
        String json = mapper.writeValueAsString(id);
        assertThat(json).isEqualTo("{\"what\":\"test\"," +
                "\"unit\":\"tests\"," +
                "\"stat\":\"max\"," +
                "\"mtype\":\"gauge\"," +
                "\"tags\":{}," +
                "\"meta\":{}}");
    }
}