package io.github.anycollect.extensions.common.expression.std;

import io.github.anycollect.extensions.common.expression.Args;
import io.github.anycollect.extensions.common.expression.EvaluationException;
import io.github.anycollect.extensions.common.expression.Expression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StdMetricIdBuilderTest {
    private StdMetricIdBuilder builder;
    private Expression tagValueExp;
    private Expression metaTagValueExp;

    @BeforeEach
    void setUp() throws EvaluationException {
        tagValueExp = mock(Expression.class);
        metaTagValueExp = mock(Expression.class);
        when(tagValueExp.process(any())).thenReturn("");
        when(metaTagValueExp.process(any())).thenReturn("");
        builder = new StdMetricIdBuilder(Collections.singletonMap("tag", tagValueExp),
                Collections.singletonMap("metaTag", metaTagValueExp));
    }

    @Test
    void argsMustBePassedToEveryExpression() throws EvaluationException {
        Args args = Args.builder().build();
        builder.create(args);
        verify(tagValueExp, times(1)).process(args);
        verify(metaTagValueExp, times(1)).process(args);
    }
}