package io.github.anycollect.extensions.common.expression.std;

import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.common.expression.Expression;
import io.github.anycollect.extensions.common.expression.ExpressionFactory;
import io.github.anycollect.extensions.common.expression.MetricIdBuilder;
import io.github.anycollect.extensions.common.expression.MetricIdBuilderFactory;
import io.github.anycollect.extensions.common.expression.ParseException;

import java.util.HashMap;
import java.util.Map;

@Extension(point = MetricIdBuilderFactory.class, name = "StdMetricIdBuilders")
public final class StdMetricIdBuilderFactory implements MetricIdBuilderFactory {
    private final ExpressionFactory expressionFactory;

    @ExtCreator
    public StdMetricIdBuilderFactory(
            @ExtDependency(qualifier = "expressions") final ExpressionFactory expressionFactory) {
        this.expressionFactory = expressionFactory;
    }

    @Override
    public MetricIdBuilder create(
            final String key, final String unit, final String stat, final String type,
            final Map<String, String> tagExpressions,
            final Map<String, String> metaTagExpressions) throws ParseException {
        Map<String, Expression> tags = parse(tagExpressions);
        Map<String, Expression> metaTags = parse(metaTagExpressions);
        return new StdMetricIdBuilder(expressionFactory.create(key),
                expressionFactory.create(unit),
                expressionFactory.create(stat),
                expressionFactory.create(type),
                tags, metaTags);
    }

    private Map<String, Expression> parse(final Map<String, String> expressionStrings) throws ParseException {
        Map<String, Expression> expressions = new HashMap<>();
        for (Map.Entry<String, String> entry : expressionStrings.entrySet()) {
            String tagKey = entry.getKey();
            String tagValue = entry.getValue();
            Expression tagValueExp = expressionFactory.create(tagValue);
            expressions.put(tagKey, tagValueExp);
        }
        return expressions;
    }
}
