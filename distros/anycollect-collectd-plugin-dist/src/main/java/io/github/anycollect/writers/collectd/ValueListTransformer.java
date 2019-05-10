package io.github.anycollect.writers.collectd;

import io.github.anycollect.core.api.filter.Filter;
import io.github.anycollect.core.api.filter.FilterChain;
import io.github.anycollect.extensions.common.expression.*;
import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.Metric;
import org.collectd.api.ValueList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ValueListTransformer {
    private final Filter filter;
    private final Expression hostExpression;
    private final Expression pluginExpression;
    private final Expression pluginInstanceExpression;
    private final Expression typeExpression;
    private final Expression typeInstanceExpression;

    public ValueListTransformer(@Nonnull final ExpressionFactory factory, @Nonnull final MappingConfig config)
            throws ParseException {
        this.filter = new FilterChain(config.filters());
        this.hostExpression = factory.create(wrap(config.host()));
        this.pluginExpression = factory.create(wrap(config.plugin()));
        this.pluginInstanceExpression = factory.create(wrap(config.pluginInstance()));
        this.typeExpression = factory.create(wrap(config.type()));
        this.typeInstanceExpression = factory.create(wrap(config.typeInstance()));
    }

    private static String wrap(final String exp) {
        return "\"" + exp + "\"";
    }

    @Nonnull
    public List<ValueList> transform(@Nonnull final Metric metric) throws EvaluationException {
        List<ValueList> result = new ArrayList<>();
        for (Measurement measurement : metric.getMeasurements()) {
            Args args = new MetricArgs(metric, measurement);
            ValueList valueList = new ValueList();
            valueList.setHost(hostExpression.process(args));
            valueList.setPlugin(pluginExpression.process(args));
            valueList.setPluginInstance(pluginInstanceExpression.process(args));
            valueList.setType(typeExpression.process(args));
            valueList.setTypeInstance(typeInstanceExpression.process(args));
            valueList.setTime(metric.getTimestamp());
            valueList.setValues(Collections.singletonList(measurement.getValue()));
            result.add(valueList);
        }
        return result;
    }

    public Filter getFilter() {
        return filter;
    }
}
