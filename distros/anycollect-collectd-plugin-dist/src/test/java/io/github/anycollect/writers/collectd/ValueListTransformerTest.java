package io.github.anycollect.writers.collectd;

import io.github.anycollect.core.impl.filters.AcceptAllFilter;
import io.github.anycollect.extensions.common.expression.std.StdExpressionFactory;
import io.github.anycollect.metric.ImmutableMetric;
import io.github.anycollect.metric.Metric;
import org.collectd.api.ValueList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ValueListTransformerTest {
    @Test
    void transformTest() throws Exception {
        Metric metric = new ImmutableMetric.Builder()
                .key("test.key")
                .at(System.currentTimeMillis())
                .tag("instance", "APP")
                .gauge(1)
                .build();
        ValueListTransformer transformer = new ValueListTransformer(new StdExpressionFactory(),
                ImmutableMappingConfig.builder()
                        .addFilters(new AcceptAllFilter())
                        .host("localhost")
                        .plugin("GenericJMX")
                        .pluginInstance("test_${instance}")
                        .type("${mtype}")
                        .typeInstance("${what}")
                        .build());
        List<ValueList> valueLists = transformer.transform(metric);
        assertThat(valueLists).hasSize(1);
        ValueList vl = valueLists.get(0);
        assertThat(vl.getHost()).isEqualTo("localhost");
        assertThat(vl.getPlugin()).isEqualTo("GenericJMX");
        assertThat(vl.getPluginInstance()).isEqualTo("test_APP");
        assertThat(vl.getType()).isEqualTo("gauge");
        assertThat(vl.getTypeInstance()).isEqualTo("test.key");
    }
}