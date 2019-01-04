package io.github.anycollect.extensions.common.expression.std;

import com.google.common.collect.ImmutableMap;
import io.github.anycollect.extensions.common.expression.Args;
import io.github.anycollect.extensions.common.expression.EvaluationException;
import io.github.anycollect.extensions.common.expression.Expression;
import io.github.anycollect.metric.MetricId;
import io.github.anycollect.metric.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StdMetricIdBuilderTest {
    private StdMetricIdBuilder builder;
    private Expression metaTagValueExp;
    private Expression keyValueExp;
    private Expression unitValueExp;
    private Expression typeValueExp;

    @BeforeEach
    void setUp() throws EvaluationException {
        keyValueExp = mock(Expression.class);
        unitValueExp = mock(Expression.class);
        typeValueExp = mock(Expression.class);
        metaTagValueExp = mock(Expression.class);
        when(keyValueExp.process(any())).thenReturn("test");
        when(unitValueExp.process(any())).thenReturn("tests");
        when(typeValueExp.process(any())).thenReturn(Type.GAUGE.getTagValue());
        when(keyValueExp.process(any())).thenReturn("metric");
        when(metaTagValueExp.process(any())).thenReturn("meta");
        builder = new StdMetricIdBuilder(
                ImmutableMap.of(
                        MetricId.METRIC_KEY_TAG, keyValueExp,
                        MetricId.UNIT_TAG, unitValueExp,
                        MetricId.METRIC_TYPE_TAG, typeValueExp
                ),
                Collections.singletonMap("metaTag", metaTagValueExp)
        );
    }

    @Test
    void argsMustBePassedToEveryExpression() throws EvaluationException {
        Args args = Args.builder().build();
        MetricId id = builder.create(args);
        verify(keyValueExp, times(1)).process(args);
        verify(unitValueExp, times(1)).process(args);
        verify(typeValueExp, times(1)).process(args);
        verify(metaTagValueExp, times(1)).process(args);
        assertThat(id.getKey()).isEqualTo("metric");
        assertThat(id.getUnit()).isEqualTo("tests");
        assertThat(id.getType()).isEqualTo(Type.GAUGE);
        assertThat(id.getMetaTagValue("metaTag")).isEqualTo("meta");
    }
}