package io.github.anycollect.extensions.common.expression.std;

import io.github.anycollect.extensions.common.expression.Args;
import io.github.anycollect.extensions.common.expression.EvaluationException;
import io.github.anycollect.extensions.common.expression.Expression;
import io.github.anycollect.extensions.common.expression.MetricIdBuilder;
import io.github.anycollect.metric.ImmutableMetricId;
import io.github.anycollect.metric.MetricId;

import java.util.Map;
import java.util.function.BiConsumer;

public final class StdMetricIdBuilder implements MetricIdBuilder {
    private final Map<String, Expression> tags;
    private final Map<String, Expression> metaTags;

    StdMetricIdBuilder(final Map<String, Expression> tags, final Map<String, Expression> metaTags) {
        this.tags = tags;
        this.metaTags = metaTags;
    }

    @Override
    public MetricId create(final Args context) throws EvaluationException {
        ImmutableMetricId.Builder builder = MetricId.builder();
        process(tags, context, builder::tag);
        process(metaTags, context, builder::meta);
        return builder.build();
    }

    private static void process(final Map<String, Expression> expressions,
                         final Args context,
                         final BiConsumer<String, String> tagConsumer) throws EvaluationException {
        for (String tagKey : expressions.keySet()) {
            Expression expression = expressions.get(tagKey);
            String tagValue = expression.process(context);
            tagConsumer.accept(tagKey, tagValue);
        }
    }
}
