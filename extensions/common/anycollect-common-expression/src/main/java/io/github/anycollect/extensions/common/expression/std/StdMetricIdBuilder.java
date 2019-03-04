package io.github.anycollect.extensions.common.expression.std;

import io.github.anycollect.extensions.common.expression.Args;
import io.github.anycollect.extensions.common.expression.EvaluationException;
import io.github.anycollect.extensions.common.expression.Expression;
import io.github.anycollect.extensions.common.expression.MetricIdBuilder;
import io.github.anycollect.metric.ImmutablePointId;
import io.github.anycollect.metric.PointId;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;

import java.util.Map;
import java.util.function.BiConsumer;

public final class StdMetricIdBuilder implements MetricIdBuilder {
    private final Expression key;
    private final Expression unit;
    private final Expression stat;
    private final Expression type;
    private final Map<String, Expression> tags;
    private final Map<String, Expression> metaTags;

    StdMetricIdBuilder(final Expression key, final Expression unit, final Expression stat, final Expression type,
                       final Map<String, Expression> tags, final Map<String, Expression> metaTags) {
        this.key = key;
        this.unit = unit;
        this.stat = stat;
        this.type = type;
        this.tags = tags;
        this.metaTags = metaTags;
    }

    @Override
    public PointId create(final Args context) throws EvaluationException {
        ImmutablePointId.Builder builder = PointId
                .key(key.process(context))
                .unit(unit.process(context))
                .stat(Stat.parse(stat.process(context)))
                .type(Type.parse(type.process(context)));
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
