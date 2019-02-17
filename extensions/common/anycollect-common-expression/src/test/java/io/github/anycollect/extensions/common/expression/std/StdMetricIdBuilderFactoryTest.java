package io.github.anycollect.extensions.common.expression.std;

import io.github.anycollect.extensions.common.expression.ExpressionFactory;
import io.github.anycollect.extensions.common.expression.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

class StdMetricIdBuilderFactoryTest {
    private ExpressionFactory expressionFactory;
    private StdMetricIdBuilderFactory factory;

    @BeforeEach
    void setUp() throws ParseException {
        expressionFactory = mock(ExpressionFactory.class);
        when(expressionFactory.create(any())).thenReturn(null);
        factory = new StdMetricIdBuilderFactory(expressionFactory);
    }

    @Test
    void mustCreateExpressionsFromTagValues() throws ParseException {
        Map<String, String> tags = spy(new HashMap<>());
        Map<String, String> metaTags = spy(new HashMap<>());
        tags.put("keyspace", "${keyspace}");
        metaTags.put("agent", "anycollect");
        factory.create("key", "ns", "max", "gauge", tags, metaTags);
        verify(expressionFactory, times(1)).create("${keyspace}");
        verify(expressionFactory, times(1)).create("anycollect");
        verify(expressionFactory, never()).create("keyspace");
        verify(expressionFactory, never()).create("agent");
    }
}