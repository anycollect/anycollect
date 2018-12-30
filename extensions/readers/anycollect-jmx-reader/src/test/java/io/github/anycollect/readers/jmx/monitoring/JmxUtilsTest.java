package io.github.anycollect.readers.jmx.monitoring;

import io.github.anycollect.metric.MetricId;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;
import org.junit.jupiter.api.Test;

import javax.management.ObjectName;

import static org.assertj.core.api.Assertions.assertThat;

class JmxUtilsTest {
    @Test
    void convertToMetricIdTest() throws Exception {
        MetricId id = JmxUtils.convert(new ObjectName("domain:what=key,mtype=gauge,unit=milli,stat=max"));
        assertThat(id.getStat()).isEqualTo(Stat.max());
        assertThat(id.getType()).isEqualTo(Type.GAUGE);
        assertThat(id.getKey()).isEqualTo("key");
        assertThat(id.getUnit()).isEqualTo("milli");
    }

    @Test
    void convertToObjectNameTest() {
        ObjectName objectName = JmxUtils.convert("test", MetricId.builder()
                .key("key")
                .unit("milli")
                .stat(Stat.max())
                .type(Type.GAUGE)
                .build());
        assertThat(objectName.getDomain()).isEqualTo("test");
        assertThat(objectName.getKeyProperty(MetricId.METRIC_KEY_TAG)).isEqualTo("key");
        assertThat(objectName.getKeyProperty(MetricId.UNIT_TAG)).isEqualTo("milli");
        assertThat(objectName.getKeyProperty(MetricId.METRIC_TYPE_TAG)).isEqualTo("gauge");
        assertThat(objectName.getKeyProperty(MetricId.STAT_TAG)).isEqualTo("max");
    }
}